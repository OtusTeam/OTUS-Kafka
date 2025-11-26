package ru.otus.spring.ex6.ask;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class Listener {

    @KafkaListener(topics = "ex6-text")
    public void onMessageReceive(@Payload String data, Acknowledgment acknowledgment) {
        log.info("onMessageReceive: {}", data);
        acknowledgment.acknowledge();
    }

}
