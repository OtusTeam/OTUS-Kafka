package ru.otus.example.ex13.db;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.utils.Utils;

import java.util.Scanner;

public class ManualPublisher {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(2, 1, "ex13-topic");

        try (
                var producer = new KafkaProducer<String, String>(Utils.producerConfig);
                var scanner = new Scanner(System.in);
        ) {
            producer.send(new ProducerRecord<>("ex13-topic", "a", "value"));
            producer.send(new ProducerRecord<>("ex13-topic", "a", "value"));
            producer.send(new ProducerRecord<>("ex13-topic", "d", "value"));
            producer.send(new ProducerRecord<>("ex13-topic", "d", "value"));
            producer.send(new ProducerRecord<>("ex13-topic", "d", "value"));
            producer.flush();

            while (true) {
                System.out.println("Enter industry: ");

                var industry = scanner.next();
                producer.send(new ProducerRecord<>("ex13-topic", industry, "value")).get();
            }
        }
    }
}
