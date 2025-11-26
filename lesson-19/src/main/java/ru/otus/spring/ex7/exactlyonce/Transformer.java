package ru.otus.spring.ex7.exactlyonce;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
@Slf4j
@RequiredArgsConstructor
public class Transformer {
    private final KafkaOperations<String, String> kafkaOperations;

    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "ex7-source")
    public void onMessageReceive(@Payload String data) {
        log.info("onMessageReceive: {}, in transaction: {}", data, kafkaOperations.inTransaction());

        kafkaOperations.send("ex7-target", data.toUpperCase()).thenAccept(result -> latch.countDown());
    }

    public void resetLatch() {
        latch = new CountDownLatch(1);
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
