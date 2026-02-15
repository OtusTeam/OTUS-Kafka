package ru.otus.p1.create.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Ex3CreateTopic {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            var topics = List.of(
                    new NewTopic("ex3-topic-1", 1, (short) 1)
                            .configs(Map.of(
                                    TopicConfig.SEGMENT_MS_CONFIG, String.valueOf(1000 * 60 * 60)
                            )),
                    new NewTopic("ex3-topic-2", Optional.empty(), Optional.empty()),
                    new NewTopic("ex3-topic-3", Map.of(0, List.of(1, 2), 1, List.of(2, 3)))
            );

            Set<String> topicNames = topics.stream()
                    .map(NewTopic::name)
                    .collect(Collectors.toSet());
            RemoveAll.checkRemoval(client, topicNames);

            var results = client.createTopics(topics);
            results.all().get();
        });
    }
}
