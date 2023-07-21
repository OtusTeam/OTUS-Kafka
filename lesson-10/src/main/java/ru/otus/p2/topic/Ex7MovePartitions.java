package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewPartitionReassignment;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Ex7MovePartitions {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex7-1",
                    Map.of(0, List.of(1, 2),
                            1, List.of(2, 3))))).all().get();

            // пошлем немного сообщений, чтобы переброс занял некоторое время
            Utils.sendMessages(0, 500, "ex7-1", 0);

            // переместим партиции
            client.alterPartitionReassignments(Map.of(
                    new TopicPartition("ex7-1", 0),
                    Optional.of(new NewPartitionReassignment(List.of(4, 5))))).all().get();

            // процесс все еще идет
            var current = client.listPartitionReassignments().reassignments().get();
            Utils.log.info("Assigments after reassign {}", current);

            Thread.sleep(1000);

            // скорее всего перемещение закончилось
            current = client.listPartitionReassignments().reassignments().get();
            Utils.log.info("Assigments after pause, {}", current);
        });
    }
}
