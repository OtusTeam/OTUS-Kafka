package ru.otus.p3.records;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import ru.otus.MessageReceiverGroup;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Ex10ConsumerGroups {
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        final String topic = "ex10";
        final String consumerGroup = "g1";

        Utils.doAdminAction(client -> {
            List<NewTopic> topics = List.of(new NewTopic(topic, 2, (short) 1));
            Set<String> topicNames = topics.stream().map(NewTopic::name)
                    .collect(Collectors.toSet());

            RemoveAll.removeAll(client);
            RemoveAll.checkRemoval(client, topicNames);

            client.createTopics(topics)
                    .all()
                    .get();

            try (var receivers = new MessageReceiverGroup(consumerGroup, 2, topic)) {
                Utils.sendMessages(0, 20, topic, null);
                /* Подождать пока потребитель прочитает сообщения */
                Thread.sleep(10_000);
            }

            var groups = client.listConsumerGroups().all().get();
            Utils.log.info("Groups\n{}", groups);

            var consumerGroupDescription = client.describeConsumerGroups(List.of(consumerGroup)).all().get();
            Utils.log.info("ConsumerGroupDescription\n{}", consumerGroupDescription);

            var offsets = client.listConsumerGroupOffsets(consumerGroup).all().get();
            Utils.log.info("ListConsumerGroupOffsets\n{}", offsets);

            /* Подождать пока потребители покинут группу */
            Thread.sleep(5_000);
            client.alterConsumerGroupOffsets(consumerGroup, Map.of(new TopicPartition(topic, 0), new OffsetAndMetadata(5)))
                    .all()
                    .get();
            Utils.log.info("======= create consumers after alter");
            try (var receiverGroup = new MessageReceiverGroup(consumerGroup, 1, topic)) {
                /* Подождать пока потребитель прочитает сообщения */
                Thread.sleep(5_000);
            }
            /* Подождать пока потребители покинут группу */
            Thread.sleep(10_000);
            Set<TopicPartition> topicPartitions = Set.of(new TopicPartition(topic, 0), new TopicPartition(topic, 1));
            client.deleteConsumerGroupOffsets(consumerGroup, topicPartitions)
                    .all()
                    .get();
        });
    }
}
