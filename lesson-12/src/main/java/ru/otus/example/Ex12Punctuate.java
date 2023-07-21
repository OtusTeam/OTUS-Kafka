package ru.otus.example;

import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.processor.Cancellable;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import ru.otus.utils.LoggingConsumer;
import ru.otus.utils.Utils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Ex12Punctuate {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "topic1", "topic2",
                "leftJoin");

        var builder = new StreamsBuilder();

        var stream1 = builder.<String, String>stream("topic1");
        var stream2 = builder.<String, String>stream("topic2");


        /* 1
        var streams = stream1.mapValues((k, v) -> "1" + v) // запоминаем источники
                .merge(stream2.mapValues((k, v) -> "2" + v)) // и соединяем в один поток
                .processValues(Generator::new) // генерим фейковое сообщение, если не было реального
                .split(Named.as("split-")) // заново разделяем
                .branch((k, v) -> v.startsWith("1"), Branched.as("1"))
                .branch((k, v) -> v.startsWith("2"), Branched.as("2"))
                .noDefaultBranch();

        var stream1x = streams.get("split-1").mapValues((k, v) -> v.substring(1)); // и возвращаем оригинальное тело
        var stream2x = streams.get("split-2").mapValues((k, v) -> v.substring(1));
        // */

        ValueJoiner<String, String, String> joiner = (a, b) -> a + "-" + b;
        var window = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(5));

        // /*
        stream1.leftJoin(stream2, joiner, window).to("leftJoin");
        // */

        /*
        stream1x.join(stream2x, joiner, window).to("leftJoin");
        // */


        try (
                var kafkaStreams = new KafkaStreams(builder.build(), Utils.createStreamsConfig(b -> {
                            b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex12-" + UUID.randomUUID().hashCode());
                            b.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class); // **
                            b.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
                        }
                ));
                var consumer = new LoggingConsumer("leftJoin", "leftJoin", Utils.consumerConfig, false);
                var producer = new Producer()
        ) {
            Utils.log.info("App Started");
            kafkaStreams.start();
            Thread.sleep(1000);

            producer.send1(1, "k", "A");
            producer.send______________________________2(1, "k", "a"); // Aa

            Thread.sleep(500);
            producer.send1(10, "k", "B");
            producer.send______________________________2(10, "k", "b"); //Bb


            Thread.sleep(500);
            producer.send1(20, "k", "C"); // Cauto


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

    private static class Generator implements FixedKeyProcessor<String, String, String> {
        private FixedKeyProcessorContext<String, String> context;
        private final Map<String, Cancellable> cancellableByKey = new HashMap<>();

        @Override
        public void process(FixedKeyRecord<String, String> record) {
            context.forward(record);

            synchronized (this) {
                var prev = cancellableByKey.remove(record.key());
                if (prev != null) {
                    Utils.log.info("Prev cancel");
                    prev.cancel();
                }

                if (record.value().startsWith("1")) {
                    cancellableByKey.put(record.key(), context.schedule(Duration.ofMillis(5), PunctuationType.WALL_CLOCK_TIME, ts -> {
                        Utils.log.info("FIRE");
                        context.forward(record.withValue("2auto").withTimestamp(record.timestamp() + 5));
                        var t = cancellableByKey.remove(record.key());
                        if (t != null) {
                            t.cancel();
                        }
                    }));
                }
            }
        }

        @Override
        public void init(FixedKeyProcessorContext<String, String> context) {
            this.context = context;
        }
    }
}
