package ru.otus.lesson17new.orders;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.OrderCreatedEvent;

@Service
@Profile("orders")
public class OrderEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderCreatedEvent publish(PlaceOrderRequest request) {
        if (request.lines() == null || request.lines().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one line");
        }

        var orderId = UUID.randomUUID().toString();
        var event = OrderCreatedEvent.from(orderId, request);

        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(Topics.ORDER_CREATED, orderId, event);
            return null;
        });

        return event;
    }
}
