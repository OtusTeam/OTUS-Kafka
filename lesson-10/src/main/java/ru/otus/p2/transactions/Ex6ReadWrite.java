package ru.otus.p2.transactions;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import ru.otus.utils.Utils;
import ru.otus.utils.AbstractConsumer;
import ru.otus.utils.LoggingConsumer;

import java.util.Map;

class Transformer extends AbstractConsumer {
    private final static String groupId = "ex6-transformer-group";

    private final KafkaProducer<String, String> producer = new KafkaProducer<>(Utils.createProducerConfig(b -> {
        b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex6-transformer");
    }));

    public Transformer(String name, String topic, Map<String, Object> config) {
        super(name, topic, config);

        producer.initTransactions();

        this.config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        this.config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        this.config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        this.thread.start();
    }

    @Override
    protected void processOne(ConsumerRecord<String, String> record) {
        producer.beginTransaction();

        producer.send(new ProducerRecord<>("outbound", record.key(), record.value() + "-processed"));
        producer.sendOffsetsToTransaction(
                Map.of(
                        new TopicPartition(record.topic(), record.partition()),
                        new OffsetAndMetadata(record.offset())),
                new ConsumerGroupMetadata(groupId));
        producer.commitTransaction();
    }

    @Override
    public void close() throws Exception {
        super.close();
        producer.close();
    }
}

public class Ex6ReadWrite {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "inbound", "outbound");

        try (
                var producer = new KafkaProducer<String, String>(Utils.producerConfig);
                var consumer = new LoggingConsumer("1", "outbound", Utils.consumerConfig, true);
                var transformer = new Transformer("transformer", "inbound", Utils.consumerConfig)) {

            producer.send(new ProducerRecord<>("inbound", "first-"));
            producer.send(new ProducerRecord<>("inbound", "second-"));


            Thread.sleep(1000);
        }

    }


}
