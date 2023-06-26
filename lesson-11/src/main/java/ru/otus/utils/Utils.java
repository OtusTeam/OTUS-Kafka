package ru.otus.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.serde.AppSerdes;

public class Utils {
    public static Logger log = LoggerFactory.getLogger("appl");

    public static final String HOST = "localhost:9091";

    public static final String STOCK_TICKER_TABLE_TOPIC = "stock-ticker-table";
    public static final String STOCK_TICKER_STREAM_TOPIC = "stock-ticker-topic";
    public static final String STOCK_TRANSACTIONS_TOPIC = "stock-transactions-topic";
    public static final String FINANCIAL_NEWS = "financial-news-topic";
    public static final String COMPANIES = "companies-topic";
    public static final String CLIENTS = "clients-topic";

    public static final Map<String, Object> producerConfig = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST,
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

    private static final Map<String, Object> streamsConfig = Map.of(
            StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, HOST);

    public static StreamsConfig createStreamsConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(streamsConfig);
        builder.accept(map);
        return new StreamsConfig(map);
    }

    public static StreamsConfig createStreamsConfig(String appId) {
        return createStreamsConfig(b -> b.put(StreamsConfig.APPLICATION_ID_CONFIG, appId));
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

    public static void recreateTopics(int numPartitions, int replicationFactor, String... topics) {
        doAdminAction(admin -> {
            RemoveAll.removeAll(admin);
            admin.createTopics(Stream.of(topics)
                    .map(it -> new NewTopic(it, numPartitions, (short) replicationFactor))
                    .toList());
            Thread.sleep(2000);
        });
    }

    public static void recreatePurchaseTopics(int partitions) {
        Utils.recreateTopics(partitions, 1,
                "purchase-topic");
    }

    public static void recreatePurchaseTopics() {
        recreatePurchaseTopics(1);
    }

    public static void recreateStockTopics(int numOfPartitions) {
        Utils.recreateTopics(numOfPartitions, 1,
                STOCK_TICKER_TABLE_TOPIC, STOCK_TICKER_STREAM_TOPIC, STOCK_TRANSACTIONS_TOPIC, FINANCIAL_NEWS, COMPANIES, CLIENTS);
    }

    public static void runPurchaseApp(StreamsBuilder builder, String name) throws Exception {
        var topology = builder.build();

        log.warn("{}", topology.describe());

        try (var kafkaStreams = new KafkaStreams(topology, Utils.createStreamsConfig(name));
             var producer = new TransactionProducer()) {
            Utils.log.info("App Started");
            kafkaStreams.start();

            producer.join();
            Thread.sleep(1000);
            Utils.log.info("Shutting down now");
        }
    }

    public static void runStockApp(StreamsBuilder builder, String name,
                                   AbstractProducer producer,
                                   Consumer<Map<String, Object>> configBuilder) throws Exception {
        runStockApp(builder, name, 1, producer, configBuilder);
    }

    public static void runStockApp(StreamsBuilder builder, String name,
                                   int numOfPartitions,
                                   AbstractProducer producer,
                                   Consumer<Map<String, Object>> configBuilder) throws Exception {
        recreateStockTopics(numOfPartitions);

        var topology = builder.build();

        log.warn("{}", topology.describe());

        try (
                var kafkaStreams = new KafkaStreams(topology, Utils.createStreamsConfig(b -> {
                    b.put(StreamsConfig.APPLICATION_ID_CONFIG, name + "-" + UUID.randomUUID().hashCode());
                    b.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);

                    configBuilder.accept(b);
                }));
                var closableProducer = producer;
        ) {
            Utils.log.info("App Started");
            kafkaStreams.start();
            producer.start();

            producer.join();
            Thread.sleep(2000);
            Utils.log.info("Shutting down now");
        }
    }

    public static <K, V> Materialized<K, V, KeyValueStore<Bytes, byte[]>> materialized(String name, Serde<K> key, Serde<V> value) {
        return Materialized.<K, V, KeyValueStore<Bytes, byte[]>>as(name)
                                .withKeySerde(key).withValueSerde(value);
    }

}
