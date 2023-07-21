package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Ex5DeleteTopic {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex5", 1, (short)1)))
                    .all().get();

            client.deleteTopics(List.of("ex5")).all().get();

            client.createTopics(List.of(new NewTopic("ex5", 1, (short)1)))
                    .all().get();
        });
    }
}
