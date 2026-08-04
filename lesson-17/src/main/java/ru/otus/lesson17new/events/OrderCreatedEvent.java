package ru.otus.lesson17new.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import ru.otus.lesson17new.orders.PlaceOrderRequest;

public record OrderCreatedEvent(
        String eventId,
        String eventType,
        String aggregateId,
        Instant occurredAt,
        String customerId,
        List<OrderLine> lines,
        BigDecimal total,
        String currency
) implements DomainEvent {

    public static OrderCreatedEvent from(String orderId, PlaceOrderRequest request) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                "OrderCreated",
                orderId,
                Instant.now(),
                request.customerId(),
                request.lines(),
                request.total(),
                request.currencyOrDefault()
        );
    }
}
