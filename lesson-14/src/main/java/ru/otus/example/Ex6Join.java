package ru.otus.example;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import ru.otus.utils.LoggingConsumer;
import ru.otus.utils.Utils;

import java.time.Duration;
import java.util.UUID;

public class Ex6Join {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "topic1", "topic2",
                "join", "leftJoin", "outerJoin");

        var builder = new StreamsBuilder();

        var stream1 = builder.<String, String>stream("topic1");
        var stream2 = builder.<String, String>stream("topic2");

        ValueJoiner<String, String, String> joiner = (a, b) -> a + "-" + b;
        var window = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(5));

        stream1.join(stream2, joiner, window).to("join");
        stream1.leftJoin(stream2, joiner, window).to("leftJoin");
        stream1.outerJoin(stream2, joiner, window).to("outerJoin");


        try (
                var kafkaStreams = new KafkaStreams(builder.build(), Utils.createStreamsConfig(b -> {
                            b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex6" + UUID.randomUUID());
                            b.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class); // **
                            b.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
                        }
                ));
                var cons1 = new LoggingConsumer("join", "join", Utils.consumerConfig, false);
                var cons2 = new LoggingConsumer("leftJoin", "leftJoin", Utils.consumerConfig, false);
                var cons3 = new LoggingConsumer("outerJoin", "outerJoin", Utils.consumerConfig, false);
                var producer = new Producer()
        ) {
            Utils.log.info("App Started");
            kafkaStreams.start();
            Thread.sleep(1000);

            producer.send1(1, "k", "A");
            producer.send______________________________2(1, "k", "a"); // Aa

            Thread.sleep(500);
            producer.send______________________________2(2, "k", "b"); //Ab
            producer.send1(3, "k", "B"); // Ba Bb

            Thread.sleep(500);
            producer.send______________________________2(10, "k", "c"); // ничего!

            Thread.sleep(500);
            producer.send1(20, "k", "C"); // все еще ничего!

            Thread.sleep(500);
            producer.send______________________________2(30, "k", "d"); // outer: null-c, C-null; left: C-null


            Thread.sleep(1000);
            Utils.log.info("Shutting down now");
        } catch (InterruptedException ignored) {
        }
    }

    private static class Producer implements AutoCloseable {
        private final KafkaProducer<String, String> producer = new KafkaProducer<String, String>(Utils.producerConfig);

        @SneakyThrows
        public void send(int topic, Integer time, String key, String value) {
            Utils.log.info("Sending to topic{} with {}: {}->{}", topic, time, key, value);
            producer.send(new ProducerRecord<>("topic" + topic, null, time == null ? null : (long)time, key, value)).get();
        }

        public void send1(Integer time, String key, String value) {
            send(1, time, key, value);
        }

        public void send______________________________2(Integer time, String key, String value) {
            send(2, time, key, value);
        }

        @Override
        public void close() throws Exception {
            producer.close();
        }
    }
}
