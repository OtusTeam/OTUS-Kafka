package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewPartitionReassignment;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionReplica;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Ex8LogDirs {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex8-1", Map.of(0, List.of(1, 2)))));

            // пошлем немного сообщений
            Utils.sendMessages(0, 500, "ex8-1", null);

            var res = client.describeLogDirs(List.of(1, 2, 3, 4, 5)).allDescriptions().get();
            Utils.log.info("Res: {}", res);

            var res2 = client.describeReplicaLogDirs(List.of(new TopicPartitionReplica("ex8-1", 0, 1)))
                    .all().get();
            Utils.log.info("Res: {}", res2);
        });
    }
}
