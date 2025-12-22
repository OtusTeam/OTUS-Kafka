package ru.otus;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

public class MessageReceiver {
    private final String consumerGroup;
    private final List<String> topics;
    private final Logger logger;

    public volatile boolean closeConsumer;

    public MessageReceiver(String consumerGroup, int no, List<String> topics) {
        this.consumerGroup = consumerGroup;
        this.topics = topics;
        this.logger = LoggerFactory.getLogger("MessagesReceiver." + consumerGroup + "." + no);

        Thread thread = new Thread(this::process);
        thread.setDaemon(true);
        thread.start();
    }

    private void process() {
        try (var consumer = new KafkaConsumer<String, String>(
                Utils.createConsumerConfig(m -> m.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup))
        )) {
            consumer.subscribe(topics);
            logger.info("Consumer subscribed");

            while (!closeConsumer) {
                var read = consumer.poll(Duration.ofMillis(100));
                for (var record : read) {
                    logger.info("Received {}.{}: {}", record.topic(), record.partition(), record.key());
                    consumer.commitAsync();
                }
            }
        }
        closeConsumer = false;
        logger.info("Consumer closed");
    }
}
