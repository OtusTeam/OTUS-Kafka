package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryRejectedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String reason
) implements DomainEvent {

    public static InventoryRejectedEvent from(OrderCreatedEvent event, String reason) {
        return new InventoryRejectedEvent(
                UUID.randomUUID().toString(),
                "InventoryRejected",
                event.aggregateId(),
                Instant.now(),
                reason
        );
    }
}
