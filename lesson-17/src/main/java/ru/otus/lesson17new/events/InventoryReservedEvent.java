package ru.otus.lesson17new.events;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        int reservedPositions
) implements DomainEvent {

    public static InventoryReservedEvent from(OrderCreatedEvent event) {
        return new InventoryReservedEvent(
                UUID.randomUUID().toString(),
                "InventoryReserved",
                event.aggregateId(),
                Instant.now(),
                event.lines().size()
        );
    }
}
