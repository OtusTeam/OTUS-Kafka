package ru.otus.example.ex10.globalktable;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
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

public class Ex10GlobalKTable2Answer {
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

        GlobalKTable<String, String> companiesTable = builder.globalTable(COMPANIES, Utils.materialized("companies-store", stringSerde, stringSerde));
        GlobalKTable<String, String> clientsTable = builder.globalTable(CLIENTS, Consumed.with(stringSerde, stringSerde), Materialized.as("clients-store"));

        countStream
                // обогащаем названием компании
                .leftJoin(companiesTable, (k, v) -> v.getStockTicker(),
                        (summary, company) -> summary.toBuilder().companyName(company).build())
                // обогащаем названием клиента
                .leftJoin(clientsTable, (k, v) -> v.getCustomerId(),
                        (summary, client) -> summary.toBuilder().customerName(client).build())
                // выводим
                .foreach((k, v) -> Utils.log.info("Result {}: {}", k, v));


        Utils.runStockApp(builder, "ex10-2", 1,
                new StockTransactionProducer(15, 50, 25, true),
                b -> {
                    b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
                });
    }
}
