package ru.otus.model.purchase;

import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Builder(toBuilder = true)
@Value
public class PurchasePattern {

    String zipCode;
    String item;
    Date date;
    double amount;

    public static final class PurchasePatternBuilder {
        public PurchasePatternBuilder from(Purchase purchase) {
            this.zipCode = purchase.getZipCode();
            this.item = purchase.getItemPurchased();
            this.date = purchase.getPurchaseDate();
            this.amount = purchase.getPrice() * purchase.getQuantity();
            return this;
        }
    }
}
