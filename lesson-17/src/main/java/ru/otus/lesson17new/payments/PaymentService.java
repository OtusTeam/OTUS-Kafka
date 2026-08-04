package ru.otus.lesson17new.payments;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.otus.lesson17new.common.DeduplicationStore;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.OrderCreatedEvent;
import ru.otus.lesson17new.events.PaymentAuthorizedEvent;
import ru.otus.lesson17new.events.PaymentRejectedEvent;

@Service
@Profile("payments")
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final DeduplicationStore deduplicationStore;
    private final BigDecimal rejectAmountThreshold;

    public PaymentService(
            KafkaTemplate<String, Object> kafkaTemplate,
            DeduplicationStore deduplicationStore,
            @Value("${app.payment.reject-amount-threshold}") BigDecimal rejectAmountThreshold
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.deduplicationStore = deduplicationStore;
        this.rejectAmountThreshold = rejectAmountThreshold;
    }

    @KafkaListener(topics = Topics.ORDER_CREATED, groupId = "lesson17-payments")
    public void onOrderCreated(OrderCreatedEvent event) {
        if (deduplicationStore.alreadyProcessed("payments", event.eventId())) {
            log.warn("Skip duplicate order event in payments: eventId={}, orderId={}", event.eventId(), event.aggregateId());
            return;
        }

        if ("fail-payment".equalsIgnoreCase(event.customerId())) {
            throw new IllegalStateException("Technical payment gateway failure for demo");
        }

        var result = event.total().compareTo(rejectAmountThreshold) > 0
                ? PaymentRejectedEvent.from(event, "Amount is greater than demo threshold " + rejectAmountThreshold)
                : PaymentAuthorizedEvent.from(event);
        var topic = result instanceof PaymentAuthorizedEvent ? Topics.PAYMENT_AUTHORIZED : Topics.PAYMENT_REJECTED;

        kafkaTemplate.executeInTransaction(operations -> {
            operations.send(topic, event.aggregateId(), result);
            return null;
        });
        deduplicationStore.markProcessed("payments", event.eventId());
        log.info("Payment decision was published: orderId={}, decision={}", event.aggregateId(), result.eventType());
    }
}
