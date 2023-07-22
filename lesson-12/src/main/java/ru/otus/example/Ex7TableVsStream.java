package ru.otus.example;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;
import ru.otus.model.purchase.CorrelatedPurchase;
import ru.otus.model.purchase.Purchase;
import ru.otus.model.stock.StockTickerData;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.StockProducer;
import ru.otus.utils.Utils;

import java.time.Duration;
import java.util.Date;

import static ru.otus.utils.Utils.STOCK_TICKER_STREAM_TOPIC;
import static ru.otus.utils.Utils.STOCK_TICKER_TABLE_TOPIC;

public class Ex7TableVsStream {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();

        KStream<String, StockTickerData> stockTickerStream = builder.stream(STOCK_TICKER_STREAM_TOPIC);
        KTable<String, StockTickerData> stockTickerTable = builder
                .table(STOCK_TICKER_TABLE_TOPIC, Materialized.as("ktable-store"));

        stockTickerStream
                .foreach((k, v) -> Utils.log.info("Stream: {}", v));
        stockTickerTable
                .toStream().foreach((k, v) -> Utils.log.info("Table: {}", v));

        Utils.runStockApp(builder, "ex5", new StockProducer(3, 3), b -> {
            b.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, AppSerdes.StockTicker.class);
            //b.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0); // **1
            //b.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1500); // **2
        });
    }
}
