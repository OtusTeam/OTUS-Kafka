package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Ex6CreatePartitions {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex6", 1, (short) 1))).all().get();

            client.createPartitions(Map.of("ex6", NewPartitions.increaseTo(3)));
        });
    }
}
