package ru.otus.p2.transactions;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.utils.Utils;
import ru.otus.utils.LoggingConsumer;

public class Ex7IsolationLevel {
    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, "topic1");

        try (
                var producerTransactional = new KafkaProducer<String, String>(Utils.createProducerConfig(b -> {
                    b.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "ex7");
                }));
                var producer = new KafkaProducer<String, String>(Utils.producerConfig);
                var consumerRC = new LoggingConsumer("ReadCommitted", "topic1", Utils.consumerConfig, true);
                var consumerRUnC = new LoggingConsumer("ReadUncommitted", "topic1", Utils.consumerConfig, false)) {

            producerTransactional.initTransactions();

            Utils.log.info("beginTransaction");
            producerTransactional.beginTransaction();
            Thread.sleep(500);
            producer.send(new ProducerRecord<>("topic1", "0")); // вне транзакции - оба получат

            Thread.sleep(500);
            producerTransactional.send(new ProducerRecord<>("topic1", "1")); // сразу получит только consumerRUnC

            Thread.sleep(500);
            producer.send(new ProducerRecord<>("topic1", "2")); // сразу получит только consumerRUnC, хотя вне транзакции

            Thread.sleep(500);
            producerTransactional.send(new ProducerRecord<>("topic1", "3")); // сразу получит только consumerRUnC

            Thread.sleep(500);
            Utils.log.info("commitTransaction");
            producerTransactional.commitTransaction(); // consumerRC получит 1,2,3

            producerTransactional.beginTransaction();
            producerTransactional.send(new ProducerRecord<>("topic1", "4")); // получит только consumerRUnC (abort)
            Thread.sleep(500);
            Utils.log.info("abortTransaction");
            producerTransactional.abortTransaction();

            producer.send(new ProducerRecord<>("topic1", "END")); // получат оба

            Thread.sleep(1000);
        }

    }
}
