package ru.otus.lesson17new.orders;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.lesson17new.events.OrderCreatedEvent;

@RestController
@RequestMapping("/orders")
@Profile("orders")
public class OrderController {
    private final OrderEventPublisher publisher;

    public OrderController(OrderEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    ResponseEntity<OrderCreatedEvent> placeOrder(@RequestBody PlaceOrderRequest request) {
        return ResponseEntity.accepted().body(publisher.publish(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> badRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
