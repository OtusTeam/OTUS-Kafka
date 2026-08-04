package ru.otus.lesson17new.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.DeduplicationStore;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.InventoryRejectedEvent;
import ru.otus.lesson17new.events.InventoryReservedEvent;
import ru.otus.lesson17new.events.OrderCreatedEvent;

@Service
@Profile("inventory")
public class InventoryService {
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DeduplicationStore deduplicationStore;

    public InventoryService(KafkaTemplate<String, Object> kafkaTemplate, DeduplicationStore deduplicationStore) {
        this.kafkaTemplate = kafkaTemplate;
        this.deduplicationStore = deduplicationStore;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "lesson17-inventory")
    public void onOrderCreated(OrderCreatedEvent event) {
        if (deduplicationStore.alreadyProcessed("inventory", event.eventId())) {
            log.warn("Skip duplicate order event in inventory: eventId={}, orderId={}", event.eventId(), event.aggregateId());
            return;
        }

        var hasOutOfStockItem = event.lines().stream()
                .anyMatch(line -> line.sku() != null && line.sku().startsWith("OUT-"));
        var result = hasOutOfStockItem
                ? InventoryRejectedEvent.from(event, "One of order items is out of stock")
                : InventoryReservedEvent.from(event);
        var topic = result instanceof InventoryReservedEvent ? Topics.INVENTORY_RESERVED : Topics.INVENTORY_REJECTED;

        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(topic, event.aggregateId(), result);
            return null;
        });
        deduplicationStore.markProcessed("inventory", event.eventId());
        log.info("Inventory decision was published: orderId={}, decision={}", event.aggregateId(), result.eventType());
    }
}
