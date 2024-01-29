package ru.otus.consumer;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.otus.model.User;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Consumer {
    public static void main(String[] args) {
        try (var consumer = new KafkaConsumer<String, User>(Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091",
                ConsumerConfig.GROUP_ID_CONFIG, "consumer",
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class,
                KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"))) {

            consumer.subscribe(List.of("topic1"));

            while (true) {
                var records = consumer.poll(Duration.ofSeconds(10));

                records.forEach(it -> System.out.println(it.key() + ": " + it.value()));
            }
        }
    }
}
