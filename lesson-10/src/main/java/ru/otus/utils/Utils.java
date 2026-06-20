package ru.otus.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    public static Logger log = LoggerFactory.getLogger("appl");

    public static final String HOST = "localhost:9091";
    public static final String HOST_PRODUCER = "localhost:9092";

    public static final Map<String, Object> producerConfig = Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_PRODUCER,
        ProducerConfig.ACKS_CONFIG, "all",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    public static Map<String, Object> createProducerConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(producerConfig);
        builder.accept(map);
        return map;
    }

    public static final Map<String, Object> consumerConfig = Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST,
        ConsumerConfig.GROUP_ID_CONFIG, "some-java-consumer",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

    public static Map<String, Object> createConsumerConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(consumerConfig);
        builder.accept(map);
        return map;
    }

    public static final Map<String, Object> adminConfig = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);

    public static Map<String, Object> createAdminConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(producerConfig);
        builder.accept(map);
        return map;
    }

    public static void doAdminAction(AdminClientConsumer action) {
        try (var client = Admin.create(Utils.adminConfig)) {
            action.accept(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void doAdminAction(Consumer<Map<String, Object>> configBuilder, AdminClientConsumer action) {
        try (var client = Admin.create(createAdminConfig(configBuilder))) {
            action.accept(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface AdminClientConsumer {
        void accept(Admin client) throws Exception;
    }

    public static final String someLongMessage;

    static {
        var b = new StringBuilder();
        for (int i = 0; i < 1024; ++i)
            b.append(i);
        someLongMessage = b.toString();
    }

    public static void sendMessages(int keyFrom, int keyTo, String topic, Integer partition) {
        try (var producer = new KafkaProducer<String, String>(Utils.producerConfig)) {
            for (int i = keyFrom; i < keyTo; i++) {
                producer.send(new ProducerRecord<>(topic, partition, Integer.toString(i),
                        Utils.someLongMessage));
            }
            producer.flush();
        }
    }

    public static void recreateTopics(int numPartitions, int replicationFactor, String ... topics) {
        doAdminAction(admin -> {
            RemoveAll.removeAll(admin);
            admin.createTopics(Stream.of(topics)
                    .map(it -> new NewTopic(it, numPartitions, (short)replicationFactor))
                    .toList());
        });
    }
}
