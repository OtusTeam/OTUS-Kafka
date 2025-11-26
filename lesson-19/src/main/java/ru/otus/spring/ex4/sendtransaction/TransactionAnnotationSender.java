package ru.otus.spring.ex4.sendtransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionAnnotationSender {
    private final KafkaTemplate<String, String> template;

    @Transactional
    public void send() {
        template.send("ex4-topic1", "Message-1");
        template.send("ex4-topic2", "Message-2");

        log.info("TransactionAnnotationSender.send: {}", template.inTransaction());
    }
}
