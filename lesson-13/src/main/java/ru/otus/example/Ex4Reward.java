package ru.otus.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Repartitioned;
import org.apache.kafka.streams.processor.StreamPartitioner;
import org.apache.kafka.streams.processor.api.FixedKeyProcessor;
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext;
import org.apache.kafka.streams.processor.api.FixedKeyRecord;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.Stores;
import ru.otus.model.purchase.Purchase;
import ru.otus.model.purchase.RewardAccumulator;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.Utils;

public class Ex4Reward {
    public static void main(String[] args) throws Exception {
        Utils.recreatePurchaseTopics(1); // **1

        var purchaseSerde = AppSerdes.purchase();
        var rewardAccumulatorSerde = AppSerdes.rewardAccumulator();
        var stringSerde = Serdes.String();

        var builder = new StreamsBuilder();

        builder.addStateStore(Stores.keyValueStoreBuilder( // **
                Stores.persistentKeyValueStore("reward-store-name"), stringSerde, Serdes.Integer()));

        builder
                .stream("purchase-topic", Consumed.with(stringSerde, purchaseSerde))
                .mapValues(p -> p.toBuilder().maskCreditCard().build())
                .peek((k, p) -> Utils.log.info("Purchase: {}", p))
                /* **2
                .selectKey((k, v) -> "some-not-null") // почему-то без ключа сообщения не уходят :(
                .repartition(Repartitioned.with(stringSerde, purchaseSerde).withStreamPartitioner(new RewardPartitioner()))
                 // */
                // пишем паттерны продаж
                .processValues(RewardProcessor::new, "reward-store-name") // **
                //
                .peek((k, p) -> Utils.log.info("Reward: {}", p))
                .to("reward-topic", Produced.with(stringSerde, rewardAccumulatorSerde));


        Utils.runPurchaseApp(builder, "ex4");
    }

    public static class RewardProcessor implements FixedKeyProcessor<String, Purchase, RewardAccumulator> {
        private FixedKeyProcessorContext<String, RewardAccumulator> context;
        private KeyValueStore<String, Integer> store;

        @Override
        public void init(FixedKeyProcessorContext<String, RewardAccumulator> context) {
            this.context = context;
            this.store = context.getStateStore("reward-store-name");
        }

        @Override
        public void process(FixedKeyRecord<String, Purchase> record) {
            var p = record.value();
            var id = RewardAccumulator.calculateId(p);
            var builder = RewardAccumulator.builder().from(p);

            Integer accumulatedBefore = store.get(id);
            if (accumulatedBefore != null) {
                builder.addRewardPoints(accumulatedBefore);
                Utils.log.warn("Found previous: {} for {}", accumulatedBefore, id);
            }

            var result = builder.build();

            store.put(id, result.getTotalRewardPoints());

            context.forward(record.withValue(result));
        }
    }

    public static class RewardPartitioner implements StreamPartitioner<String, Purchase> {
        @Override
        public Integer partition(String topic, String key, Purchase value, int numPartitions) {
            var customerId = RewardAccumulator.calculateId(value);
            return Math.abs(customerId.hashCode()) % numPartitions;
        }
    }
}
