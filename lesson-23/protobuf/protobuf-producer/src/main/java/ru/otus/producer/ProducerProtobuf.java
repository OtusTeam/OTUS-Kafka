package ru.otus.producer;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import ru.otus.PersonOuterClass.Person;
import ru.otus.PersonOuterClass.Persons;

import java.util.Map;

public class ProducerProtobuf {
    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, Persons>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9091",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class,
                KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081"))) {

            var u1 = Person.newBuilder()
                .setName("Ivan")
                .setFavoriteNumber(10)
                .setFavoriteColor("blue")
                .setEmail("a@b.ru")
                .build();
            var u2 = Person.newBuilder()
                .setName("Petr")
                .setFavoriteNumber(42)
                .setFavoriteColor("red")
                .setEmail("b@c.ru")
                .build();
            var users = Persons.newBuilder()
                .addPerson(u1).addPerson(u2)
                .build();

            producer.send(new ProducerRecord<>("topic-protobuf", "2", users));
        }
    }
}
