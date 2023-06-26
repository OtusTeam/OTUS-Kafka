package ru.otus.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.SessionWindows;
import org.apache.kafka.streams.kstream.Windowed;
import ru.otus.model.stock.StockTransaction;
import ru.otus.model.stock.TransactionSummary;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.StockTransactionProducer;
import ru.otus.utils.Utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static ru.otus.utils.Utils.STOCK_TRANSACTIONS_TOPIC;

public class Ex9Window {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> transactionSerde = AppSerdes.stockTransaction();
        Serde<TransactionSummary> transactionKeySerde = AppSerdes.transactionSummary();

        var twentySeconds = Duration.ofSeconds(20);
        var fifteenMinutes = Duration.ofMinutes(15);
        KTable<Windowed<TransactionSummary>, Long> customerTransactionCounts = builder
                .stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, transactionSerde))
                .groupBy((noKey, transaction) -> TransactionSummary.from(transaction).build(),
                        Grouped.with(transactionKeySerde, transactionSerde))
                .windowedBy(SessionWindows.ofInactivityGapAndGrace(twentySeconds, fifteenMinutes))
                .count();
        customerTransactionCounts.toStream()
                .foreach((k, v) -> {
                    Utils.log.info("Window {}: {}", k, v);
                });

        KStream<String, TransactionSummary> countStream = customerTransactionCounts.toStream().map((window, count) -> {
            TransactionSummary transactionSummary = window.key();
            String newKey = transactionSummary.getIndustry();
            return KeyValue.pair(newKey, transactionSummary.toBuilder()
                    .summaryCount(count)
                    .build());
        });
        countStream.foreach((k, v) -> {
            Utils.log.info("Count {}: {}", k, v);
        });

        Utils.runStockApp(builder, "ex8",
                new StockTransactionProducer(15, 50, 25, false),
                b -> {
                    b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
                });
    }
}
