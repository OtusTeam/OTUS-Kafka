package ru.otus.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import ru.otus.utils.Producer;
import ru.otus.utils.Utils;

import java.util.UUID;

@Slf4j
public class Ex102Transaction {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "ex102-src-topic");

        Serde<String> stringSerde = Serdes.String();
        var builder = new StreamsBuilder();

        builder
                .stream("ex102-src-topic", Consumed.with(stringSerde, stringSerde))
                .peek((k, v) -> log.info("topologyA: {}: {}", k, v))
                .map((k, v) -> KeyValue.pair(k + "x", v.toUpperCase()))
                .repartition(Repartitioned.with(stringSerde, stringSerde)) // 2 топологии !!!
                .peek((k, v) -> log.info("topologyB: {}: {}", k, v))
                .to("ex102-out-topic", Produced.with(stringSerde, stringSerde));

        Utils.log.info("{}", builder.build().describe());

        try (var kafkaStreams = new KafkaStreams(builder.build(),
                Utils.createStreamsConfig(b -> {
                    b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex102-" + UUID.randomUUID());
                    b.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2); // 2) закомментируем
                    b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000); // 1) увеличим до 5000
                }));
             var producer = new Producer("ex102-src-topic", b -> b.key("1").value("message-" + b.no), 100)) {
            Thread.sleep(100);
            Utils.log.info("App Started");
            kafkaStreams.start();

            Thread.sleep(20000);

            Utils.log.info("Shutting down now");
        }
    }
}
