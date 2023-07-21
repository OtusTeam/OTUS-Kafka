package ru.otus.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import ru.otus.model.purchase.Purchase;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.Utils;

public class Ex2PurchaseSimple {
    public static void main(String[] args) throws Exception {
        Utils.recreatePurchaseTopics();

        var purchaseSerde = AppSerdes.purchase();
        var purchasePatternSerde = AppSerdes.purchasePattern();
        var rewardAccumulatorSerde = AppSerdes.rewardAccumulator();
        var stringSerde = Serdes.String();

        var builder = new StreamsBuilder();

        KStream<String, Purchase> purchaseKStream = builder
                .stream("purchase-topic", Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> p.toBuilder().maskCreditCard().build());
        ///* // 2
        purchaseKStream.print(Printed.<String, Purchase>toSysOut().withLabel("purchases"));
        purchaseKStream.to("purchase-masked-topic", Produced.with(stringSerde, purchaseSerde));
        // */

         /* // 2
        purchaseKStream
                .peek((k, p) -> Utils.log.info("Masked purchase: {}", p))
                .to("purchase-masked-topic", Produced.with(stringSerde, purchaseSerde));
        // */

        /* // 1
        KStream<String, PurchasePattern> patternKStream = purchaseKStream.mapValues(p -> PurchasePattern.builder().from(p).build());
        patternKStream.print(Printed.<String, PurchasePattern>toSysOut().withLabel("patterns"));
        patternKStream.to("pattern-topic", Produced.with(stringSerde, purchasePatternSerde));

        KStream<String, RewardAccumulator> rewardsKStream = purchaseKStream.mapValues(p -> RewardAccumulator.builder().from(p).build());
        rewardsKStream.print(Printed.<String, RewardAccumulator>toSysOut().withLabel("rewards"));
        rewardsKStream.to("reward-topic", Produced.with(stringSerde, rewardAccumulatorSerde));
        // */

        Utils.runPurchaseApp(builder, "ex2");
    }
}
