package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewPartitionReassignment;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Ex7MovePartitions {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            List<NewTopic> topics = List.of(new NewTopic("ex7-1",
                    Map.of(0, List.of(1, 2),
                            1, List.of(2, 3))));
            Set<String> topicNames = topics.stream().map(NewTopic::name)
                    .collect(Collectors.toSet());

            RemoveAll.checkRemoval(client, topicNames);

            client.createTopics(topics).all().get();

            // пошлем немного сообщений, чтобы переброс занял некоторое время
            Utils.sendMessages(0, 500, "ex7-1", 0);

            // переместим партиции
            client.alterPartitionReassignments(Map.of(
                    new TopicPartition("ex7-1", 0),
                    Optional.of(new NewPartitionReassignment(List.of(1, 3))))).all().get();

            // процесс все еще идет
            var current = client.listPartitionReassignments().reassignments().get();
            Utils.log.info("Assignments after reassign {}", current);

            Thread.sleep(1000);

            // скорее всего перемещение закончилось
            current = client.listPartitionReassignments().reassignments().get();
            Utils.log.info("Assignments after pause, {}", current);
        });
    }
}
