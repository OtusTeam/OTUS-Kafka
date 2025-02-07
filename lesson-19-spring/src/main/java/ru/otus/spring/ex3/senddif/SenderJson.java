package ru.otus.spring.ex3.senddif;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.spring.model.SomeModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class SenderJson {
    private final KafkaTemplate<String, SomeModel> template;

    @PostConstruct
    public void send() {
        template.send("ex3-json", "Hello", new SomeModel(42, "Ivan"));
    }
}
