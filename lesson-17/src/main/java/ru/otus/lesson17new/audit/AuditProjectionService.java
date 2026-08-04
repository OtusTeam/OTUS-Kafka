package ru.otus.lesson17new.audit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.DomainEvent;
import ru.otus.lesson17new.events.InventoryRejectedEvent;
import ru.otus.lesson17new.events.InventoryReservedEvent;
import ru.otus.lesson17new.events.OrderCancelledEvent;
import ru.otus.lesson17new.events.OrderConfirmedEvent;
import ru.otus.lesson17new.events.OrderCreatedEvent;
import ru.otus.lesson17new.events.PaymentAuthorizedEvent;
import ru.otus.lesson17new.events.PaymentRejectedEvent;

@Service
@Profile("audit")
public class AuditProjectionService {
    private final Map<String, List<DomainEvent>> eventsByOrderId = new ConcurrentHashMap<>();

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "lesson17-audit")
    public void onOrderCreated(OrderCreatedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_AUTHORIZED, groupId = "lesson17-audit")
    public void onPaymentAuthorized(PaymentAuthorizedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.PAYMENT_REJECTED, groupId = "lesson17-audit")
    public void onPaymentRejected(PaymentRejectedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.INVENTORY_RESERVED, groupId = "lesson17-audit")
    public void onInventoryReserved(InventoryReservedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.INVENTORY_REJECTED, groupId = "lesson17-audit")
    public void onInventoryRejected(InventoryRejectedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.ORDER_CONFIRMED, groupId = "lesson17-audit")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        remember(event);
    }

    @KafkaListener(topics = Topics.ORDER_CANCELLED, groupId = "lesson17-audit")
    public void onOrderCancelled(OrderCancelledEvent event) {
        remember(event);
    }

    public List<DomainEvent> eventsFor(String orderId) {
        return eventsByOrderId.getOrDefault(orderId, List.of()).stream()
                .sorted(Comparator.comparing(DomainEvent::occurredAt))
                .toList();
    }

    public Map<String, List<DomainEvent>> allOrders() {
        return eventsByOrderId;
    }

    private void remember(DomainEvent event) {
        eventsByOrderId.compute(event.aggregateId(), (orderId, events) -> {
            var copy = events == null ? new ArrayList<DomainEvent>() : new ArrayList<>(events);
            copy.add(event);
            return copy;
        });
    }
}
