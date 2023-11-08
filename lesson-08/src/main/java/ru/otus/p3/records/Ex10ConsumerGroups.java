package ru.otus.p3.records;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import ru.otus.MessageReceiverGroup;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;

public class Ex10ConsumerGroups {

    public static void main(String[] args) {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex10", 2, (short)1))).all().get();

            try(var receiverGroup = new MessageReceiverGroup("g1", 2, "ex10")) {
                Thread.sleep(1000);

                Utils.sendMessages(0, 20, "ex10", null);

                Thread.sleep(1000);
            }

            var groups = client.listConsumerGroups().all().get();
            Utils.log.info("Groups\n{}", groups);

            var consumerGroupDescription = client.describeConsumerGroups(List.of("g1")).all().get();
            Utils.log.info("ConsumerGroupDescription\n{}", consumerGroupDescription);

            var offsets = client.listConsumerGroupOffsets("g1").all().get();
            Utils.log.info("ListConsumerGroupOffsets\n{}", offsets);

            Utils.log.info("======= create consumers again");
            try(var receiverGroup = new MessageReceiverGroup("g1", 1, "ex10")) {
                Thread.sleep(1000);
            }

            client.alterConsumerGroupOffsets("g1", Map.of(new TopicPartition("ex10", 0), new OffsetAndMetadata(5))).all().get();
            Utils.log.info("======= create consumers after alter");
            try(var receiverGroup = new MessageReceiverGroup("g1", 1, "ex10")) {
                Thread.sleep(1000);
            }
        });
    }
}
