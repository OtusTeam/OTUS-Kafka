package ru.otus.lesson17new.orders;

import java.math.BigDecimal;
import java.util.List;
import ru.otus.lesson17new.events.OrderLine;

public record PlaceOrderRequest(
        String customerId,
        List<OrderLine> lines,
        String currency
) {
    public BigDecimal total() {
        if (lines == null || lines.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return lines.stream()
                .map(line -> line.price().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String currencyOrDefault() {
        return currency == null || currency.isBlank() ? "RUB" : currency;
    }
}
