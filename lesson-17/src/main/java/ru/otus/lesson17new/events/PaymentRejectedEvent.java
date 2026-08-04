package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentRejectedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String reason
) implements DomainEvent {

    public static PaymentRejectedEvent from(OrderCreatedEvent event, String reason) {
        return new PaymentRejectedEvent(
                UUID.randomUUID().toString(),
                "PaymentRejected",
                event.aggregateId(),
                Instant.now(),
                reason
        );
    }
}
