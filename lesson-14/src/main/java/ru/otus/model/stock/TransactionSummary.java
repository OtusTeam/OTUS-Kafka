package ru.otus.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
@AllArgsConstructor
public class TransactionSummary {

    String customerId;
    String stockTicker;
    String industry;
    long summaryCount;
    String customerName;
    String companyName;

    public static TransactionSummaryBuilder from(StockTransaction transaction){
        return builder()
                .customerId(transaction.getCustomerId())
                .stockTicker(transaction.getSymbol())
                .industry(transaction.getIndustry());
    }
}
