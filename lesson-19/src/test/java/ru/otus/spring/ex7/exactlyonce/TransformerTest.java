package ru.otus.spring.ex7.exactlyonce;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"ex7-target", "ex7-source"})
class TransformerTest {
    @Autowired
    private KafkaOperations<String, String> operations;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;


    @Autowired
    private Transformer transformer;

    private Consumer<String, String> consumer;

    @BeforeEach
    void beforeEach() {
        transformer.resetLatch();

        var config = KafkaTestUtils.consumerProps("test", "true", embeddedKafkaBroker);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumer = new KafkaConsumer<>(config);
        consumer.subscribe(List.of("ex7-target"));
    }

    @AfterEach
    void afterEach() {
        consumer.close();
    }

    @Test
    void test() throws Exception {
        operations.send("ex7-source", "input");

        boolean messageConsumed = transformer.getLatch().await(10, TimeUnit.SECONDS);
        assertThat(messageConsumed).isTrue();

        var received = consumer.poll(Duration.ofSeconds(10));
        assertThat(received.count()).isEqualTo(1);
        assertThat(received.iterator().next().value()).isEqualTo("INPUT");
    }

}