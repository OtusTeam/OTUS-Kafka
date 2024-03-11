package ru.otus.serde;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import ru.otus.model.purchase.CorrelatedPurchase;
import ru.otus.model.purchase.Purchase;
import ru.otus.model.purchase.PurchasePattern;
import ru.otus.model.purchase.RewardAccumulator;
import ru.otus.model.stock.FixedSizePriorityQueue;
import ru.otus.model.stock.ShareVolume;
import ru.otus.model.stock.StockTickerData;
import ru.otus.model.stock.StockTransaction;
import ru.otus.model.stock.TransactionSummary;

public class AppSerdes {
    private static <T> Serde<T> serde(Class<T> cls) {
        return new Serdes.WrapperSerde<>(new JsonSerializer<>(), new JsonDeserializer<>(cls));
    }
    public static Serde<Purchase> purchase() {
        return serde(Purchase.class);
    }

    public static Serde<PurchasePattern> purchasePattern() {
        return serde(PurchasePattern.class);
    }

    public static Serde<RewardAccumulator> rewardAccumulator() {
        return serde(RewardAccumulator.class);
    }

    public static Serde<CorrelatedPurchase> correlatedPurchase() {
        return serde(CorrelatedPurchase.class);
    }

    public static Serde<StockTickerData> stockTicker() {
        return new StockTicker();
    }

    public static Serde<StockTransaction> stockTransaction() {
        return serde(StockTransaction.class);
    }

    public static Serde<ShareVolume> shareVolume() {
        return serde(ShareVolume.class);
    }

    public static Serde<FixedSizePriorityQueue<ShareVolume>> fixedSizePriorityQueue() {
        return new Serdes.WrapperSerde<>(new JsonSerializer<>(),
                new JsonDeserializer<FixedSizePriorityQueue<ShareVolume>>(FixedSizePriorityQueue.class));
    }

    public static Serde<TransactionSummary> transactionSummary() {
        return serde(TransactionSummary.class);
    }


    public static final class StockTicker extends Serdes.WrapperSerde<StockTickerData> {
        public StockTicker() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(StockTickerData.class));
        }
    }
}
