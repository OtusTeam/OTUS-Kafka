package ru.otus;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.otus.kafka.Student;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class AvroProducerExample {

    public static void sendStudent(String key, Student student) throws ExecutionException, InterruptedException {

        //1 - Конфигурация
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "http://localhost:19092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        //2 - Создать Producer с соответствующими типами key и value
        final KafkaProducer<String, Student> producer = new KafkaProducer<>(props);

        //3 - Создать ProducerRecord
        final ProducerRecord<String, Student> record =
                new ProducerRecord<>("students", key, student);

        //4 - Отправить сообщение
        producer.send(record).get();
        producer.close();

    }
}
