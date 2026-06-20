package ru.otus.spring.ex8.receivejson;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.otus.spring.model.SomeModel;

import java.util.List;

@Service
@Slf4j
public class Listener {
    @KafkaListener(topics = "ex8-json")
    public void onMessageReceive(@Payload SomeModel model) {
        log.info("onMessageReceive: {}", model);
    }
}
