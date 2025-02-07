package ru.otus.utils;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;

public class Producer extends AbstractProducer {
    private final String topic;
    private final Builder builder;
    private final ProducerConsumer afterSend;

    public Producer(String topic, Map<String, Object> config, Builder builder, ProducerConsumer afterSend, boolean autoStart) {
        super(topic, config);
        this.topic = topic;
        this.builder = builder;
        this.afterSend = afterSend;

        if (autoStart) thread.start();

    }

    public Producer(String topic, Builder builder, int timeout) {
        this(topic, Utils.producerConfig, builder,
                p -> {
                    p.flush();
                    Thread.sleep(timeout);
                },
                true);
    }

    @Override
    protected void doSend(KafkaProducer<String, String> producer) throws Exception {
        int count = 0;
        while (!Thread.interrupted()) {
            var recordBuilder = new RecordBuilder(count);
            count += 1;

            builder.accept(recordBuilder);

            var record = new ProducerRecord<>(topic, recordBuilder.key, recordBuilder.value);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    Utils.log.info("Sender to {} CANT send {}", topic, recordBuilder.key);
                } else {
                    Utils.log.info("Send {}.{}", recordBuilder.key, recordBuilder.value);
                }
            });
            afterSend.accept(producer);
        }
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class RecordBuilder {
        public final int no;
        public String key;
        public String value;

        public RecordBuilder(int no) {
            this.no = no;
        }
    }

    public interface Builder {
        void accept(RecordBuilder b) throws Exception;
    }

    public interface ProducerConsumer {
        void accept(KafkaProducer<String, String> p) throws Exception;
    }
}
