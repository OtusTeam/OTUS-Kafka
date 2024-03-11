package ru.otus.producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.otus.model.User;

import java.util.Map;

public class Producer {
    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, User>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class,
                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"))) {

            producer.send(new ProducerRecord<>("topic1", "2", User.newBuilder()
                    .setName("Ivan")
                    // /*1*/ .setEmail("a@b.ru")
                    .setFavoriteColor("blue")
                    .setFavoriteNumber(42)
                    .build()));
        }
    }
}
