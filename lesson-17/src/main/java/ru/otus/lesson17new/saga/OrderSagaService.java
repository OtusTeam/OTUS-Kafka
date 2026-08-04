package ru.otus.lesson17new.saga;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.DomainEvent;
import ru.otus.lesson17new.events.InventoryRejectedEvent;
import ru.otus.lesson17new.events.InventoryReservedEvent;
import ru.otus.lesson17new.events.OrderConfirmedEvent;
import ru.otus.lesson17new.events.PaymentAuthorizedEvent;
import ru.otus.lesson17new.events.PaymentRejectedEvent;

@Service
@Profile("saga")
public class OrderSagaService {
    private static final Logger log = LoggerFactory.getLogger(OrderSagaService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, OrderSagaState> states = new ConcurrentHashMap<>();

    public OrderSagaService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = Topics.PAYMENT_AUTHORIZED, groupId = "lesson17-saga")
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        apply(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_REJECTED, groupId = "lesson17-saga")
    public void onPaymentRejected(PaymentRejectedEvent event) {
        apply(event);
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "lesson17-saga")
    public void onInventoryReserved(InventoryReservedEvent event) {
        apply(event);
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED, groupId = "lesson17-saga")
    public void onInventoryRejected(InventoryRejectedEvent event) {
        apply(event);
    }

    private void apply(DomainEvent event) {
        var state = states.computeIfAbsent(event.aggregateId(), OrderSagaState::new);
        synchronized (state) {
            state.apply(event);
            state.finalEventIfReady().ifPresent(this::publishFinalEvent);
        }
    }

    private void publishFinalEvent(DomainEvent event) {
        var topic = event instanceof OrderConfirmedEvent ? Topics.ORDER_CONFIRMED : Topics.ORDER_CANCELLED;
        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(topic, event.aggregateId(), event);
            return null;
        });
        log.info("Saga final event was published: orderId={}, event={}", event.aggregateId(), event.eventType());
    }
}
