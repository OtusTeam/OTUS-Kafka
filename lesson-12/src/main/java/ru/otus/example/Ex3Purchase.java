package ru.otus.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import ru.otus.model.purchase.Purchase;
import ru.otus.model.purchase.PurchasePattern;
import ru.otus.model.purchase.RewardAccumulator;
import ru.otus.serde.AppSerdes;
import ru.otus.service.SecurityDBService;
import ru.otus.utils.Utils;

import java.util.Map;

public class Ex3Purchase {
    public static void main(String[] args) throws Exception {
        Utils.recreatePurchaseTopics();

        var purchaseSerde = AppSerdes.purchase();
        var purchasePatternSerde = AppSerdes.purchasePattern();
        var rewardAccumulatorSerde = AppSerdes.rewardAccumulator();
        var stringSerde = Serdes.String();
        var longSerde = Serdes.Long();

        var builder = new StreamsBuilder();

        KStream<String, Purchase> purchaseKStream = builder
                .stream("purchase-topic", Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> p.toBuilder().maskCreditCard().build());
        // фильтруем, меняем ключ и пишем маскированные
        purchaseKStream
                .filter((k, p) -> p .getPrice() > 5.0, Named.as("price-filter"))  // ** 1
                .selectKey((k, p) -> p.getPurchaseDate().getTime()) // ** 1
                .peek((k, p) -> Utils.log.info("Masked purchase: {}->{}", k, p))
                .to("purchase-masked-topic", Produced.with(longSerde, purchaseSerde));

        /* // ** 2
        // Сохраняем отдельно кафе и электронику
        Map<String, KStream<String, Purchase>> kstreamByDept = purchaseKStream.split(Named.as("split-"))
                .branch((k, p) -> p.getDepartment().equalsIgnoreCase("coffee"), Branched.as("coffee"))
                .branch((k, p) -> p.getDepartment().equalsIgnoreCase("electronics"), Branched.as("electronics"))
                .noDefaultBranch();
        kstreamByDept.get("split-coffee").to( "coffee-topic", Produced.with(stringSerde, purchaseSerde));
        kstreamByDept.get("split-electronics").to( "electronics-topic", Produced.with(stringSerde, purchaseSerde));
        // */

        /* // ** 3
        purchaseKStream
                .filter((key, purchase) -> purchase.getEmployeeId().equals("000000"))
                .foreach((k, p) ->
                        SecurityDBService.saveRecord(p.getPurchaseDate(), p.getEmployeeId(), p.getItemPurchased()));
        // */

        // пишем паттерны продаж
        purchaseKStream.mapValues(p -> PurchasePattern.builder().from(p).build())
                .peek((k, p) -> Utils.log.info("Pattern: {}", p))
                .to("pattern-topic", Produced.with(stringSerde, purchasePatternSerde));

        // пишем награды
        purchaseKStream.mapValues(p -> RewardAccumulator.builder().from(p).build())
                .peek((k, p) -> Utils.log.info("Reward: {}", p))
                .to("reward-topic", Produced.with(stringSerde, rewardAccumulatorSerde));

        Utils.runPurchaseApp(builder, "ex3");
    }
}
