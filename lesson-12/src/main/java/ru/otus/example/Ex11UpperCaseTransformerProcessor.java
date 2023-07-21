package ru.otus.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import ru.otus.utils.Producer;
import ru.otus.utils.Utils;

public class Ex11UpperCaseTransformerProcessor {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "ex1-src-topic");

        Serde<String> stringSerde = Serdes.String();

        Topology toplogy = new Topology();
        toplogy
                .addSource("source", stringSerde.deserializer(), stringSerde.deserializer(), "ex1-src-topic")
                .addProcessor("processor", CaseTransformer::new, "source")
                .addProcessor("logger", Logger::new, "processor")
                .addSink("sink", "ex1-out-topic", stringSerde.serializer(), stringSerde.serializer(), "processor");


        Utils.log.info("{}", toplogy.describe());

        try (var kafkaStreams = new KafkaStreams(toplogy, Utils.createStreamsConfig("ex11"));
             var producer = new Producer("ex1-src-topic", b -> b.key("1").value("message-" + b.no), 500)) {
            Utils.log.info("App Started");
            kafkaStreams.start();

            Thread.sleep(2000);

            Utils.log.info("Shutting down now");
        }
    }

    static class CaseTransformer implements Processor<String, String, String, String> {
        private ProcessorContext<String, String> context;
        @Override
        public void init(ProcessorContext<String, String> context) {
            this.context = context;
        }

        @Override
        public void process(Record<String, String> record) {
            context.forward(new Record<>(record.key(), record.value().toUpperCase(), record.timestamp()));
        }
    }

    static class Logger implements Processor<String, String, String, String> {

        @Override
        public void process(Record<String, String> record) {
            Utils.log.info("{}: {}", record.key(), record.value());
        }
    }
}
