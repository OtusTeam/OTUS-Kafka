package ru.otus.model.purchase;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Builder(toBuilder = true)
@Value
public class CorrelatedPurchase {

    String customerId;
    @Singular("itemPurchased")
    List<String> itemsPurchased;
    double totalAmount;
    Date firstPurchaseTime;
    Date secondPurchaseTime;
}
