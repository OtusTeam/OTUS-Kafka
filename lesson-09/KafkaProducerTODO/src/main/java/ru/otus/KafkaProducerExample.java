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

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, <TODO>);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, <TODO>);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, <TODO>);

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String k1 = "student001";
        String v1 = "{'Name': 'Ivan', 'Surname': 'Ivanov'}";
        ProducerRecord<TODO, TODO> record1 =
                new ProducerRecord<>(topicName, TODO, TODO);

        String k2 = "student002";
        String v2 = "{'Name': 'Anna', 'Surname': 'Popova'}";
        ProducerRecord<TODO, TODO> record2 =
                new ProducerRecord<>(topicName, TODO, TODO);

        String k3 =  "student003";
        String v3 = "{'Name': 'Petr', 'Surname': 'Petrov'}";
        ProducerRecord<TODO, TODO> record3 =
                new ProducerRecord<>(topicName,TODO, TODO);

        //Send first message with blocks until completed
        producer.send(TODO)

        //Send second message async: callback with lambda expression
        producer.send(TODO, TODO);

        //Send third message: Async with a callback class
        producer.send(record3, TODO);

        //Wait for all previously sent messages, then close
        producer.close();

        //OR Wait for 60 seconds, then close
        producer.close(TODO);

    }
}
