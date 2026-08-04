package ru.otus.lesson17new.notifications;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.OrderCancelledEvent;
import ru.otus.lesson17new.events.OrderConfirmedEvent;

@Service
@Profile("notifications")
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @KafkaListener(topics = Topics.ORDER_CONFIRMED, groupId = "lesson17-notifications")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        log.info("Notify customer: order {} was confirmed", event.aggregateId());
    }

    @KafkaListener(topics = Topics.ORDER_CANCELLED, groupId = "lesson17-notifications")
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Notify customer: order {} was cancelled, reason={}", event.aggregateId(), event.reason());
    }

    @KafkaListener(topics = Topics.ORDER_CREATED_DLT, groupId = "lesson17-notifications-dlt")
    public void onDeadLetter(ConsumerRecord<String, Object> record) {
        log.error("Dead letter received: topic={}, key={}, value={}", record.topic(), record.key(), record.value());
    }
}
