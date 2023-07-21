package ru.otus.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import ru.otus.utils.Producer;
import ru.otus.utils.Utils;

public class Ex1UpperCaseTransformer {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "ex1-src-topic");

        Serde<String> stringSerde = Serdes.String();
        var builder = new StreamsBuilder();

        KStream<String, String> simpleFirstStream = builder.stream("ex1-src-topic", Consumed.with(stringSerde, stringSerde));
        KStream<String, String> upperCasedStream = simpleFirstStream.mapValues(it -> it.toUpperCase());
        upperCasedStream.to("ex1-out-topic", Produced.with(stringSerde, stringSerde));
        // upperCasedStream.print(Printed.<String, String>toSysOut().withLabel("Ex1 App")); // 1 - второй сток

        /* // 2 - более краткая гусеничная запись
        var upperCasedStream = builder
                .stream("ex1-src-topic", Consumed.with(stringSerde, stringSerde))
                .mapValues(it -> it.toUpperCase());
        upperCasedStream.to("ex1-out-topic", Produced.with(stringSerde, stringSerde));
        upperCasedStream.print(Printed.<String, String>toSysOut().withLabel("Ex1 App"));
*/
        Utils.log.info("{}", builder.build().describe());

        try (var kafkaStreams = new KafkaStreams(builder.build(), Utils.createStreamsConfig("ex1"));
             var producer = new Producer("ex1-src-topic", b -> b.key("1").value("message-" + b.no), 500)) {
            Utils.log.info("App Started");
            kafkaStreams.start();

            Thread.sleep(2000);

            Utils.log.info("Shutting down now");
        }
    }
}
