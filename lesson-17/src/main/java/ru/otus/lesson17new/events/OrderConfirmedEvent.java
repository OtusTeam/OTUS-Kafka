package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt
) implements DomainEvent {

    public static OrderConfirmedEvent forOrder(String orderId) {
        return new OrderConfirmedEvent(
                UUID.randomUUID().toString(),
                "OrderConfirmed",
                orderId,
                Instant.now()
        );
    }
}
