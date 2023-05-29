package ru.otus;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.InterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class MessageReceiver implements AutoCloseable {
    private final Thread thread = new Thread(this::process);
    private final String consumerGroup;
    private final List<String> topics;
    private final Logger logger;

    public MessageReceiver(String consumerGroup, int no, List<String> topics) {
        this.consumerGroup = consumerGroup;
        this.topics = topics;
        this.logger = LoggerFactory.getLogger("MessagesReceiver." + consumerGroup + "." + no);

        thread.setDaemon(true);
        thread.start();
    }

    private void process() {
        try (var consumer = new KafkaConsumer<String, String>(Utils.createConsumerConfig(m ->
            m.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup)))) {

            consumer.subscribe(topics);
            logger.info("Subscribed");

            try {
                while (true) {
                    var read = consumer.poll(Duration.ofSeconds(1));
                    for (var record : read) {
                        logger.info("Receive {}.{}: {}", record.topic(), record.partition(), record.key());
                    }
                }
            } catch (InterruptException ignored) {
                Thread.interrupted();
            }
        }
        logger.info("Complete!");
    }

    @Override
    public void close() throws Exception {
        thread.interrupt();
        thread.join();
    }
}
