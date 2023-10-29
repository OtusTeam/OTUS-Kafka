package ru.otus;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import java.time.Duration;

public class KafkaProducerExample {

    public static String topicName = "students";

    public static void getKafkaExample() throws ExecutionException, InterruptedException {

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String k1 = "student001";
        String v1 = "{'Name': 'Ivan', 'Surname': 'Ivanov'}";
        ProducerRecord<String, String> record1 =
                new ProducerRecord<>(topicName, k1, v1);

        String k2 = "student002";
        String v2 = "{'Name': 'Anna', 'Surname': 'Popova'}";
        ProducerRecord<String, String> record2 =
                new ProducerRecord<>(topicName, k2, v2);

        String k3 =  "student003";
        String v3 = "{'Name': 'Petr', 'Surname': 'Petrov'}";
        ProducerRecord<String, String> record3 =
                new ProducerRecord<>(topicName, k3, v3);

        //Send first message with blocks until completed
        producer.send(record1).get();

        //Send second message async: callback with lambda expression
        producer.send(record2, (recordMetadata, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println("Message sent: " + record2.key());
            }
        });

        //Send third message: Async with a callback class
        producer.send(record3, new CustomCallback());

        //Wait for all previously sent messages, then close
        producer.close();

        //OR Wait for 60 seconds, then close
        producer.close(Duration.ofSeconds(60));

    }
}
