package ru.otus.lesson17new.saga;

import java.util.Optional;
import ru.otus.lesson17new.events.DomainEvent;
import ru.otus.lesson17new.events.InventoryRejectedEvent;
import ru.otus.lesson17new.events.InventoryReservedEvent;
import ru.otus.lesson17new.events.OrderCancelledEvent;
import ru.otus.lesson17new.events.OrderConfirmedEvent;
import ru.otus.lesson17new.events.PaymentAuthorizedEvent;
import ru.otus.lesson17new.events.PaymentRejectedEvent;

public class OrderSagaState {
    private final String orderId;
    private boolean paymentAuthorized;
    private boolean inventoryReserved;
    private String cancellationReason;
    private boolean finalEventPublished;

    public OrderSagaState(String orderId) {
        this.orderId = orderId;
    }

    public void apply(DomainEvent event) {
        if (event instanceof PaymentAuthorizedEvent) {
            paymentAuthorized = true;
        } else if (event instanceof InventoryReservedEvent) {
            inventoryReserved = true;
        } else if (event instanceof PaymentRejectedEvent rejected) {
            cancellationReason = "payment: " + rejected.reason();
        } else if (event instanceof InventoryRejectedEvent rejected) {
            cancellationReason = "inventory: " + rejected.reason();
        }
    }

    public Optional<DomainEvent> finalEventIfReady() {
        if (finalEventPublished) {
            return Optional.empty();
        }
        if (cancellationReason != null) {
            finalEventPublished = true;
            return Optional.of(OrderCancelledEvent.forOrder(orderId, cancellationReason));
        }
        if (paymentAuthorized && inventoryReserved) {
            finalEventPublished = true;
            return Optional.of(OrderConfirmedEvent.forOrder(orderId));
        }
        return Optional.empty();
    }
}
