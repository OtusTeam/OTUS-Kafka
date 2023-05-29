package ru.otus;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.errors.TopicExistsException;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Удаление всех созданных топиков с экспериментами в асинхронность
 */
public class RemoveAll {

    // Просто, переходим к синхронному коду
    private static Collection<String> sync(Admin client) throws Exception {
        var topics = client.listTopics()
                .listings()
                .get() // вот тот самый переход
                .stream()
                .map(TopicListing::name)
                .toList();

        Utils.log.info("External topics: {}", topics);

        client.deleteTopics(topics).all()
                .get(); // а вот еще раз

        Utils.log.info("SUCCESS");

        return topics;
    }

    // Попытка сделать асинхронно на KafkaFuture (неудачная)
    private static void async1(Admin client) throws Exception {
        client.listTopics()
                .listings()
                .thenApply(listings -> listings.stream()
                        .filter(it -> !it.isInternal())
                        .map(TopicListing::name)
                        .toList())
                .thenApply(topics -> {
                    Utils.log.info("External topics: {}", topics);
                    return client.deleteTopics(topics).all();
                })
                // см тип результата :(
                .get()
                .get();
    }

    // Сложнее, асинхронный код на CompletionStage
    private static void async2(Admin client) throws Exception {
        client.listTopics()
                .listings()
                .toCompletionStage()
                .thenApply(listings -> listings.stream()
                        .filter(it -> !it.isInternal())
                        .map(TopicListing::name)
                        .toList())
                .thenCompose(topics -> {
                    Utils.log.info("External topics: {}", topics);
                    return client.deleteTopics(topics).all().toCompletionStage();
                })
                .whenComplete((ignored, ex) -> {
                    if (ex == null)
                        Utils.log.info("SUCCESS");
                    else
                        Utils.log.error("Troubles...", ex);
                })
                .toCompletableFuture()
                .get();
    }

    private static void async3(Admin client) throws Exception {
        Mono.fromCompletionStage(client.listTopics()
                        .listings()
                        .toCompletionStage())
                .map(listings -> listings.stream()
                        .filter(it -> !it.isInternal())
                        .map(TopicListing::name)
                        .toList())
                .doOnSuccess(topics -> Utils.log.info("External topics: {}", topics))
                .flatMap(topics -> Mono.fromCompletionStage(
                        client.deleteTopics(topics).all().toCompletionStage()
                ))
                .doOnSuccess(ignored -> Utils.log.info("SUCCESS"))
                .doOnError(ex -> Utils.log.error("Troubles...", ex))
                .block();
    }

    public static void main(String[] args) {
        Utils.doAdminAction(client -> {
            sync(client);
            // async1(client);
            // async2(client);
            // async3(client);
        });
    }

    public static void removeAll(Admin client) throws Exception {
        var topics = sync(client);

        // не нашел лучшего варианта. После удаления на сомом деле топики еще не удалены и создать их нельзя
        var newTopics = topics.stream().map(t -> new NewTopic(t, 1, (short) 1)).toList();var options = new CreateTopicsOptions().validateOnly(true);

        while (true) {
            try {
                client.createTopics(newTopics, options).all().get();
                break;
            } catch (ExecutionException ex) {
                if (ex.getCause() == null || ex.getCause().getClass() != TopicExistsException.class)
                    throw ex;
                Thread.sleep(100);
            }
        }
    }
}
