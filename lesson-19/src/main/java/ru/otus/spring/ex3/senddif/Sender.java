package ru.otus.spring.ex3.senddif;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Sender {
    private final KafkaTemplate<String, String> template;

    @PostConstruct
    public void send() {
        template.send("ex3-string", "Hello", "Hello from spring!");
    }
}
