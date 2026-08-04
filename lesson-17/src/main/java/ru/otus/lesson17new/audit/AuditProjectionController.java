package ru.otus.lesson17new.audit;

import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.lesson17new.events.DomainEvent;

@RestController
@RequestMapping("/audit")
@Profile("audit")
public class AuditProjectionController {
    private final AuditProjectionService service;

    public AuditProjectionController(AuditProjectionService service) {
        this.service = service;
    }

    @GetMapping("/orders")
    Map<String, List<DomainEvent>> orders() {
        return service.allOrders();
    }

    @GetMapping("/orders/{orderId}")
    List<DomainEvent> eventsFor(@PathVariable String orderId) {
        return service.eventsFor(orderId);
    }
}
