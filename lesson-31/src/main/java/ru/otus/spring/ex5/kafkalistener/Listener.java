package ru.otus.spring.ex5.kafkalistener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class Listener {

    //@KafkaListener(topics = "ex5-text") // или
    public void onMessageReceiveText(@Payload String data) {
        log.info("onMessageReceiveText: {}", data);
    }

    //@KafkaListener(topics = "ex5-text") // или
    public void onMessageReceiveKeyValue(
            @Payload String data,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) String timestamp) {
        log.info("onMessageReceiveKeyValue: {}-{} at {}}", key, data, timestamp);
    }

    //@KafkaListener(topics = "ex5-text") // или
    public void onMessageReceiveRecord(ConsumerRecord<String, String> message) {
        log.info("onMessageReceiveRecord: {}", message);
    }

    @KafkaListener(topics = "ex5-text", batch = "true") // или
    public void onMessageReceiveBatch(List<ConsumerRecord<String, String>> messages) {
        log.info("onMessageReceiveBatch: {}", messages);
    }
}
