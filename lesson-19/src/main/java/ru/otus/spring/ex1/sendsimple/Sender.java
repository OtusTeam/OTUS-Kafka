package ru.otus.spring.ex1.sendsimple;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class Sender {
    private final KafkaTemplate<String, String> template;

    @PostConstruct
    public void send() {
        template.send("ex1-topic1", "Hello", "Hello from spring!").thenAccept(action ->
                log.info("Send complete: {}", action.getRecordMetadata())
        );

        template.send("ex1-topic1", "", "Some message").thenAccept(action ->
                log.info("Send complete: {}", action.getRecordMetadata())
        );

        log.info("Added to sending queue");
    }
}
