package ru.otus.p3.records;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.common.TopicPartition;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Ex9DeleteRecords {

    public static void main(String[] args) {
        Utils.doAdminAction(client -> {
            List<NewTopic> topics = List.of(new NewTopic("ex9", 2, (short) 1));
            Set<String> topicNames = topics.stream().map(NewTopic::name)
                    .collect(Collectors.toSet());

            RemoveAll.removeAll(client);
            RemoveAll.checkRemoval(client, topicNames);

            client.createTopics(topics).all()
                    .whenComplete((ignored, ex) ->
                            Utils.sendMessages(0, 500, "ex9", 0)
                    )
                    .toCompletionStage()
                    .thenCompose(ignored ->
                            client.deleteRecords(Map.of(new TopicPartition("ex9", 0), RecordsToDelete.beforeOffset(250)))
                            .all()
                            .toCompletionStage())
                    .toCompletableFuture()
                    .join();
        });
    }
}
