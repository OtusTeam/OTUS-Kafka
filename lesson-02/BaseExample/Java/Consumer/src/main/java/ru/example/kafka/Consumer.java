package ru.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class Consumer {
    public static void main(String[] args) {
        final String BOOTSTRAP_SERVERS = "localhost:9092";
        final String topic = "test";
        final String GROUP_ID = "g1";
        final String OFFSET_RESET = "earliest";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFSET_RESET);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);

        try {
            consumer.subscribe(Collections.singleton(topic));
            while (true) {
                ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<Integer, String> data : records) {
                    String message = String.format("%d\t%d\t%d\t%s", data.partition(), data.offset(), data.key(), data.value());
                    System.out.println(message);
                }
            }
        } catch (WakeupException e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            consumer.close();
        }
    }
}
