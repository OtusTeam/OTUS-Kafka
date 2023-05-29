package ru.otus.p1.create.topic;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import ru.otus.RemoveAll;

import java.util.List;
import java.util.Map;

public class Ex1CreateTopic {

    public static void main(String[] args) throws Exception {
        Map<String, Object> properties = Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091");

        try (var client = Admin.create(properties)) {
            var requestedTopic = new NewTopic("ex1-topic", 2, (short) 2);

            var topicResult = client.createTopics(List.of(requestedTopic));
            // не факт, что топик уже создан, пользоваться им нельзя

            topicResult.all().get();
            // топик точно создан, можете пользоваться
        }
    }

}
