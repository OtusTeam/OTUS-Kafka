package ru.otus.example.ex10.globalktable;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.SessionWindows;
import ru.otus.model.stock.StockTransaction;
import ru.otus.model.stock.TransactionSummary;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.StockTransactionProducer;
import ru.otus.utils.Utils;

import java.time.Duration;

import static ru.otus.utils.Utils.CLIENTS;
import static ru.otus.utils.Utils.COMPANIES;
import static ru.otus.utils.Utils.STOCK_TRANSACTIONS_TOPIC;

public class Ex10GlobalKTable1Answer {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();

        Serde<String> stringSerde = Serdes.String();
        Serde<StockTransaction> transactionSerde = AppSerdes.stockTransaction();
        Serde<TransactionSummary> transactionKeySerde = AppSerdes.transactionSummary();

        var twentySeconds = Duration.ofSeconds(20);

        // поток industry: TransactionSummary (customerId, stockTicker, industry, summaryCount)
        KStream<String, TransactionSummary> countStream = builder
                .stream(STOCK_TRANSACTIONS_TOPIC, Consumed.with(stringSerde, transactionSerde))
                .groupBy((noKey, transaction) -> TransactionSummary.from(transaction).build(),
                        Grouped.with(transactionKeySerde, transactionSerde))
                .windowedBy(SessionWindows.ofInactivityGapWithNoGrace(twentySeconds))
                .count(Materialized.with(transactionKeySerde, new Serdes.LongSerde()))
                .toStream()
                .filter((k, v) -> v != null)
                .map((window, count) -> {
                    TransactionSummary transactionSummary = window.key();
                    String newKey = transactionSummary.getIndustry();
                    return KeyValue.pair(newKey, transactionSummary.toBuilder()
                            .summaryCount(count)
                            .build());
                })
                .peek((k, v) -> Utils.log.info("Source {}: {}", k, v));

        KTable<String, String> companiesTable = builder.table(COMPANIES, Utils.materialized("companies-store", stringSerde, stringSerde));
        KTable<String, String> clientsTable = builder.table(CLIENTS, Consumed.with(stringSerde, stringSerde), Materialized.as("clients-store"));

        countStream
                // обогащаем названием компании
                .selectKey((k, v) -> v.getStockTicker())
                .leftJoin(companiesTable, (summary, company) -> summary.toBuilder().companyName(company).build(),
                        Joined.with(stringSerde, transactionKeySerde, stringSerde))
                // обогащаем названием клиента
                .selectKey((k, v) -> v.getCustomerId())
                .leftJoin(clientsTable, (summary, client) -> summary.toBuilder().customerName(client).build(),
                        Joined.with(stringSerde, transactionKeySerde, stringSerde))
                // возвращаем ключ обратно и выводим
                .selectKey((k, v) -> v.getIndustry())
                .foreach((k, v) -> Utils.log.info("Result {}: {}", k, v));


        Utils.runStockApp(builder, "ex10-1", 1,
                new StockTransactionProducer(15, 50, 25, true),
                b -> {
                    b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
                });
    }
}
