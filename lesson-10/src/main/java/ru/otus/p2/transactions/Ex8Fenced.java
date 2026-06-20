package ru.otus.p2.transactions;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import ru.otus.utils.Utils;
import ru.otus.utils.LoggingConsumer;

public class Ex8Fenced {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "topic1");

        try (
                var producer1 = new KafkaProducer<String, String>(Utils.createProducerConfig(b -> {
                    b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex8");
                }));
                var consumerRC = new LoggingConsumer("ReadCommitted", "topic1", Utils.consumerConfig, true)) {

            producer1.initTransactions();
            producer1.beginTransaction();
            producer1.send(new ProducerRecord<>("topic1", "1"));
            producer1.commitTransaction();

            try (var producer2 = new KafkaProducer<String, String>(Utils.createProducerConfig(b -> {
                b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex8");
            }))) {
                producer2.initTransactions();
                producer2.beginTransaction();
                producer2.send(new ProducerRecord<>("topic1", "2 from 2"));
                producer2.commitTransaction();
            }

            try {
                producer1.beginTransaction();
                producer1.send(new ProducerRecord<>("topic1", "2 from 1"));
                producer1.commitTransaction();
            } catch (ProducerFencedException ex) {
                Utils.log.error("Error", ex);
            }

            Thread.sleep(500);
        }
    }
}
