package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record PaymentAuthorizedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String authorizationCode
) implements DomainEvent {

    public static PaymentAuthorizedEvent from(OrderCreatedEvent event) {
        return new PaymentAuthorizedEvent(
                UUID.randomUUID().toString(),
                "PaymentAuthorized",
                event.aggregateId(),
                Instant.now(),
                "AUTH-" + event.aggregateId().substring(0, 8)
        );
    }
}
