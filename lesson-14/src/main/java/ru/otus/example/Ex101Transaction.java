package ru.otus.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import ru.otus.utils.Producer;
import ru.otus.utils.Utils;

import java.util.UUID;

public class Ex101Transaction {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "ex101-src-topic");

        Serde<String> stringSerde = Serdes.String();
        var builder = new StreamsBuilder();

        var upperCasedStream = builder
                .stream("ex101-src-topic", Consumed.with(stringSerde, stringSerde))
                .mapValues(it -> it.toUpperCase());
        upperCasedStream.to("ex101-out-topic", Produced.with(stringSerde, stringSerde));
        upperCasedStream.print(Printed.<String, String>toSysOut().withLabel("Ex1 App"));

        Utils.log.info("{}", builder.build().describe());

        try (var kafkaStreams = new KafkaStreams(builder.build(),
                Utils.createStreamsConfig(b -> {
                    b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex101" + UUID.randomUUID());
                    b.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
                }))) {
            Utils.log.info("App Started");
            Thread.sleep(1000);
            kafkaStreams.start();

            try (var producer = new Producer("ex101-src-topic", b -> b.key("1").value("message-" + b.no), 500)) {
                Thread.sleep(2000);
            }

            Thread.sleep(2000);

            Utils.log.info("Shutting down now");
        }
    }
}
