package ru.otus;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerKafka {

    public static String topicName = "students";

    public static void consumerExample() {

        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, TODO);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, TODO);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TODO);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(TODO);

        try {
            while (true) {
                ConsumerRecords<TODO, TODO> records = consumer.poll(TODO);
                for (ConsumerRecord<TODO, TODO> record : records)
                    System.out.printf("offset = %d, key = %s, value = %s",
                            TODO, TODO, TODO);
            }
        } finally {
            consumer.close();
        }

    }
}
