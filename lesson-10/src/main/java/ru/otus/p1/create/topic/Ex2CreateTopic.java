package ru.otus.p1.create.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import ru.otus.Utils;

import java.util.List;

public class Ex2CreateTopic {

    public static void main(String[] args) throws Exception {
        Utils.doAdminAction(client -> {
            var requestedTopic = new NewTopic("ex2-topic", 2, (short) 2);

            var topicResult = client.createTopics(List.of(requestedTopic));
            // не факт, что топик уже создан, пользоваться им нельзя

            topicResult.all().get();
            // топик точно создан, можете пользоваться
        });
    }

}
