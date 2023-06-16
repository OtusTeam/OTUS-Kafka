package ru.otus.utils;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.common.errors.TopicExistsException;

import java.util.Collection;
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

    public static void main(String[] args) {
        Utils.doAdminAction(RemoveAll::sync);
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
