package ru.otus.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import ru.otus.model.purchase.CorrelatedPurchase;
import ru.otus.model.purchase.Purchase;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.Utils;

import java.time.Duration;
import java.util.Date;

public class Ex5Join {
    public static void main(String[] args) throws Exception {
        Utils.recreatePurchaseTopics();

        var purchaseSerde = AppSerdes.purchase();
        var correlationPurchaseSerde = AppSerdes.correlatedPurchase();
        var stringSerde = Serdes.String();

        var builder = new StreamsBuilder();

        KStream<String, Purchase> purchaseKStream = builder
                .stream("purchase-topic", Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> p.toBuilder().maskCreditCard().build());

        // Сохраняем отдельно кафе и электронику
        var kstreamByDept = purchaseKStream
                .selectKey((k, p) -> p.getCustomerId()) // установим ключ. Это приведет к автоматическому репартиционированию
                //.repartition(Repartitioned.with(stringSerde, purchaseSerde)) // **2 но его можно сделать и явно
                .split(Named.as("split-"))
                .branch((k, p) -> p.getDepartment().equalsIgnoreCase("coffee"), Branched.as("coffee"))
                .branch((k, p) -> p.getDepartment().equalsIgnoreCase("electronics"), Branched.as("electronics"))
                .noDefaultBranch();
        var coffeeStream = kstreamByDept.get("split-coffee");
        var electronicStream = kstreamByDept.get("split-electronics");

        // джойним
        coffeeStream
                .join(electronicStream,
                        new Joiner(),
                        JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(20)),
                        StreamJoined.with(stringSerde, purchaseSerde, purchaseSerde)
                ) // **
                .peek((k, v) -> Utils.log.info("Correlated: {}", v))
                .to("correlated-topic", Produced.with(stringSerde, correlationPurchaseSerde));

        Utils.runPurchaseApp(builder, "ex5");
    }

    public static class Joiner implements ValueJoiner<Purchase, Purchase, CorrelatedPurchase> {
        @Override
        public CorrelatedPurchase apply(Purchase purchase, Purchase otherPurchase) {

            var builder = CorrelatedPurchase.builder();

            Date purchaseDate = purchase != null ? purchase.getPurchaseDate() : null;
            Double price = purchase != null ? purchase.getPrice() : 0.0;
            String itemPurchased = purchase != null ? purchase.getItemPurchased() : null;

            Date otherPurchaseDate = otherPurchase != null ? otherPurchase.getPurchaseDate() : null;
            Double otherPrice = otherPurchase != null ? otherPurchase.getPrice() : 0.0;
            String otherItemPurchased = otherPurchase != null ? otherPurchase.getItemPurchased() : null;

            if (itemPurchased != null) {
                builder.itemPurchased(itemPurchased);
            }

            if (otherItemPurchased != null) {
                builder.itemPurchased(otherItemPurchased);
            }

            String customerId = purchase != null ? purchase.getCustomerId() : null;
            String otherCustomerId = otherPurchase != null ? otherPurchase.getCustomerId() : null;

            return builder.customerId(customerId != null ? customerId : otherCustomerId)
                    .firstPurchaseTime(purchaseDate)
                    .secondPurchaseTime(otherPurchaseDate)
                    .totalAmount(price + otherPrice)
                    .build();
        }
    }
}
