package ru.otus.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import ru.otus.model.purchase.Purchase;
import ru.otus.model.purchase.RewardAccumulator;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TransactionProducer extends AbstractProducer {
    private final int numberPurchases;
    private final int numberIterations;
    private final int numberCustomers;

    public TransactionProducer(int numberPurchases, int numberIterations, int numberCustomers) {
        super("purchase-topic", Utils.producerConfig);

        this.numberPurchases = numberPurchases;
        this.numberIterations = numberIterations;
        this.numberCustomers = numberCustomers;

        thread.start();
    }

    public TransactionProducer() {
        this(20, 2, 5);
    }

    @Override
    protected void doSend(KafkaProducer<String, String> producer) throws Exception {
        int counter = 0;
        int total = 0;
        int[] partitions = {0};

        var bonusByCustomer = new HashMap<String, Integer>();

        Utils.doAdminAction(admin -> {
            partitions[0] = admin.describeTopics(List.of("purchase-topic")).topicNameValues().get("purchase-topic").get().partitions().size();
        });
        var random = new Random();

        while (counter++ < numberIterations  && !Thread.interrupted()) {
            List<Purchase> purchases = DataGenerator.generatePurchases(numberPurchases, numberCustomers);
            total += purchases.size();
            for (var purchase : purchases) {
                bonusByCustomer.compute(RewardAccumulator.calculateId(purchase), (key, prev) -> {
                    var amount = (int)(purchase.getQuantity() * purchase.getPrice());
                    return prev == null ? amount : prev + amount;
                });

                ProducerRecord<String, String> record = new ProducerRecord<>(
                        "purchase-topic", random.nextInt(partitions[0]), null, convertToJson(purchase));
                producer.send(record);
            }
            producer.flush();
            Utils.log.info("Record batch sent: {}", total);
            Thread.sleep(1000);
        }

        Utils.log.info("BonusByCustomer: {}", bonusByCustomer);
    }
}
