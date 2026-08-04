package ru.otus.lesson17new.events;

import java.time.Instant;

public interface DomainEvent {
    String eventId();

    String eventType();

    String aggregateId();

    Instant occurredAt();
}
