package ru.otus.model.purchase;

import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.Objects;

@Builder(toBuilder = true)
@Value
public class Purchase {

    String firstName;
    String lastName;
    String customerId;
    String creditCardNumber;
    String itemPurchased;
    String department;
    String employeeId;
    int quantity;
    double price;
    Date purchaseDate;
    String zipCode;
    String storeId;

    public static final class PurchaseBuilder {
        private static final String CC_NUMBER_REPLACEMENT = "xxxx-xxxx-xxxx-";

        public PurchaseBuilder maskCreditCard() {
            Objects.requireNonNull(this.creditCardNumber, "Credit Card can't be null");
            String[] parts = this.creditCardNumber.split("-");
            if (parts.length < 4) {
                this.creditCardNumber = "xxxx";
            } else {
                String last4Digits = this.creditCardNumber.split("-")[3];
                this.creditCardNumber = CC_NUMBER_REPLACEMENT + last4Digits;
            }
            return this;
        }
    }
}
