package ru.otus.p2.topic;

import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Ex5DeleteTopic {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            List<NewTopic> topics = List.of(new NewTopic("ex5", 1, (short) 1));
            var topicNames = topics.stream()
                    .map(NewTopic::name)
                    .collect(Collectors.toSet());

            RemoveAll.checkRemoval(client, topicNames);
            client.createTopics(topics)
                    .all().get();

            client.deleteTopics(topicNames).all()
                    .get();

            RemoveAll.checkRemoval(client, topicNames);

            client.createTopics(topics)
                    .all().get();
            Utils.log.info("Ex5DeleteTopic topic recreated");
        });
    }
}
