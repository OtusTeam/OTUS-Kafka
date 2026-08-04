package ru.otus.lesson17new.events;

import java.math.BigDecimal;

public record OrderLine(
        String sku,
        int quantity,
        BigDecimal price
) {
}
