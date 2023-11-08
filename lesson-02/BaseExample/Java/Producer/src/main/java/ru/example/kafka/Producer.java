package ru.example.kafka;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws InterruptedException {
        final String BOOTSTRAP_SERVERS = "localhost:9092";
        String topic = "test";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        org.apache.kafka.clients.producer.Producer<Integer, String> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 1000; i++) {
                ProducerRecord<Integer, String> data = new ProducerRecord<>(topic, i, "Message " + i);
                producer.send(data);
                System.out.println("Send: Message " + i);
                TimeUnit.SECONDS.sleep(2);
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }
}