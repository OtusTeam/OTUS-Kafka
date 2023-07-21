package ru.otus;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import ru.otus.kafka.Student;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class AvroConsumerExample {

    public static Student readStudent() {

        //1 - Конфигурация
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, <TODO>);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, <TODO>);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, <TODO>);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, <TODO>);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, <TODO>);
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, "true");

        //2 - Создать Producer с соответствующими типами key и value
        KafkaConsumer<TODO, TODO> consumer = new KafkaConsumer<>(props);

        //3 - Подписаться на топик students
        consumer.subscribe(<TODO>);

        //4 Прочитать сообщение
        while(true) {
            ConsumerRecords<TODO, TODO> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<TODO, TODO> record: records) {
                System.out.printf("Key: %s, Value: %s\n",
                        record.key(), record.value().toString());
            }
        }

    }
}
