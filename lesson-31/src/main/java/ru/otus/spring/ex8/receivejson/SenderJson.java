package ru.otus.spring.ex8.receivejson;

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
        operations.send("ex8-json", new SomeModel(42, "Ivan"));
        operations.send("ex8-json", new SomeModel(42, "Stepan"));
        operations.flush();
    }
}
