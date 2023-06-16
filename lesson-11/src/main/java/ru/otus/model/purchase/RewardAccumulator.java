package ru.otus.model.purchase;

import lombok.Builder;
import lombok.Value;

@Builder(toBuilder = true)
@Value
public class RewardAccumulator {

    String customerId;
    double purchaseTotal;
    int totalRewardPoints;
    int currentRewardPoints;
    int daysFromLastPurchase;

    public static final class RewardAccumulatorBuilder {
        public RewardAccumulatorBuilder from(Purchase purchase) {
           customerId = calculateId(purchase);
           purchaseTotal = purchase.getPrice() * (double) purchase.getQuantity();
           currentRewardPoints = (int) purchaseTotal;
           totalRewardPoints = currentRewardPoints;
           return this;
        }

        public RewardAccumulatorBuilder addRewardPoints(int previousTotalPoints) {
            this.totalRewardPoints += previousTotalPoints;
            return this;
        }
    }

    public static String calculateId(Purchase purchase) {
        return purchase.getLastName()+","+purchase.getFirstName();
    }
}
