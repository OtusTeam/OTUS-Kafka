package ru.otus.spring.ex2.sendjson;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.stereotype.Service;
import ru.otus.spring.model.SomeModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderJson {
    private final KafkaOperations<String, SomeModel> operations;

    @PostConstruct
    public void send() {
        operations.send("ex2-json", "Hello", new SomeModel(42, "Ivan"));
    }
}
