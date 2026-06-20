package ru.otus.spring.ex4.sendtransaction;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class Sender {
    private final KafkaTemplate<String, String> template;

    private final TransactionAnnotationSender transactionAnnotationSender;

    @PostConstruct
    public void start() {
        //transactionAnnotationSender.send();
        //sendNoTransaction(); // так не работает! Надо вызывать по ссылке, полученной от спринга
        sendInTransaction();
    }

    @Transactional
    public void sendNoTransaction() {
        template.send("ex4-topic1", "Message-3");
        template.send("ex4-topic2", "Message-4");
        template.flush();

        log.info("Sender.sendNoTransaction: {}", template.inTransaction());
    }

    public void sendInTransaction() {
        template.executeInTransaction(ops -> {
            template.send("ex4-topic1", "Message-5");
            template.send("ex4-topic2", "Message-6");

            log.info("Sender.sendInTransaction: {}", template.inTransaction());
            return 1;
        });
    }
}
