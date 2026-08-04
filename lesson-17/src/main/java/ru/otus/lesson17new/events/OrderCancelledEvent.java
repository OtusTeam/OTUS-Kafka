package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String reason
) implements DomainEvent {

    public static OrderCancelledEvent forOrder(String orderId, String reason) {
        return new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                "OrderCancelled",
                orderId,
                Instant.now(),
                reason
        );
    }
}
