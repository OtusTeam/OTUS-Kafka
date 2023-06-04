package ru.otus.p2.transactions;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.utils.Utils;
import ru.otus.utils.LoggingConsumer;

public class Ex5Transaction {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "topic1", "topic2");

        try (
                var producer = new KafkaProducer<String, String>(Utils.createProducerConfig(b -> {
                    b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex5");
                }));
                var consumer = new LoggingConsumer("1", "topic1", Utils.consumerConfig, true)) {

            producer.initTransactions();

            for (int i = 0; i < 4; ++i) {
                producer.beginTransaction();
                producer.send(new ProducerRecord<>("topic1", "some-" + i));
                producer.send(new ProducerRecord<>("topic2", "other-" + i));
                producer.commitTransaction();
            }

            Thread.sleep(1000);
        }

    }
}
