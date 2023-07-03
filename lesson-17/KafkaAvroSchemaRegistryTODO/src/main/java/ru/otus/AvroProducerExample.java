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
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, <TODO>);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, <TODO>);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, <TODO>);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, <TODO>);

        //2 - Создать Producer с соответствующими типами key и value
        final KafkaProducer<TODO, TODO> producer = new KafkaProducer<>(props);

        //3 - Создать ProducerRecord
        final ProducerRecord<TODO, TODO> record =
                new ProducerRecord<>(<TODO>);

        //4 - Отправить сообщение
        producer.send(<TODO>).get();
        producer.close();

    }
}
