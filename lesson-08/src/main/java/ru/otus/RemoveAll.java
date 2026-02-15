package ru.otus;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.errors.TopicExistsException;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Удаление всех созданных топиков с экспериментами в асинхронность
 */
public class RemoveAll {

    // Просто, переходим к синхронному коду
    public static void removeAll(Admin client) throws Exception {
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
    }

    // Сложнее, асинхронный код на CompletionStage
    public static void removeAllInChain(Admin client) throws Exception {
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
                .join();
    }

    public static void removeAllWithMono(Admin client) throws Exception {
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
        Utils.doAdminAction(RemoveAll::removeAll);
        Utils.doAdminAction(RemoveAll::removeAllInChain);
        Utils.doAdminAction(RemoveAll::removeAllWithMono);
    }

    public static void checkRemoval(Admin client, Set<String> topics) throws InterruptedException {
        if (topics.isEmpty()) {
            return;
        }

        // не нашел лучшего варианта. После удаления на самом деле топики еще не удалены и создать их нельзя
        // https://kafka.apache.org/26/javadoc/org/apache/kafka/clients/admin/KafkaAdminClient.html#deleteTopics-java.util.Collection-org.apache.kafka.clients.admin.DeleteTopicsOptions-
        var newTopics = topics.stream().map(t -> new NewTopic(t, 1, (short) 1)).toList();
        var options = new CreateTopicsOptions().validateOnly(true);

        boolean doesExist = true;
        while (doesExist) {
            try {
                client.createTopics(newTopics, options).all().get();
                doesExist = false;
            } catch (Exception ex) {
                Thread.sleep(100);
                Utils.log.error("exception: {}", ex.getMessage());
            }
        }
        Utils.log.info("topics {} doesn't exist", topics);
    }
}
