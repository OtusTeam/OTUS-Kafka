package ru.otus.example.ex12;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import ru.otus.model.stock.StockPerformance;
import ru.otus.model.stock.StockTransaction;
import ru.otus.serde.AppSerdes;
import ru.otus.utils.Utils;

import java.util.UUID;

import static ru.otus.example.ex12.MockDataProducer.STOCK_TRANSACTIONS_TOPIC;

@Slf4j
public class Ex12StockPerformance {

    public static void main(String[] args) throws Exception {
        Utils.recreateTopics(1, 1, STOCK_TRANSACTIONS_TOPIC);

        StreamsConfig streamsConfig = Utils.createStreamsConfig(b -> {
            b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex12-" + UUID.randomUUID());
            b.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
            b.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
            b.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            b.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
            b.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        });
        Deserializer<String> stringDeserializer = Serdes.String().deserializer();
        Serializer<String> stringSerializer = Serdes.String().serializer();
        Serde<StockPerformance> stockPerformanceSerde = AppSerdes.stockPerformance();
        Serializer<StockPerformance> stockPerformanceSerializer = stockPerformanceSerde.serializer();
        Serde<StockTransaction> stockTransactionSerde = AppSerdes.stockTransaction();
        Deserializer<StockTransaction> stockTransactionDeserializer = stockTransactionSerde.deserializer();

        Topology topology = new Topology();
        String stocksStateStore = "stock-performance-store";
        double differentialThreshold = 0.02;

        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(stocksStateStore);
        StoreBuilder<KeyValueStore<String, StockPerformance>> storeBuilder = Stores.keyValueStoreBuilder(storeSupplier, Serdes.String(), stockPerformanceSerde);

        topology.addSource("stocks-source", stringDeserializer, stockTransactionDeserializer, STOCK_TRANSACTIONS_TOPIC)
                .addProcessor("stocks-processor", () -> new StockPerformanceProcessor(stocksStateStore, differentialThreshold), "stocks-source")
                .addStateStore(storeBuilder,"stocks-processor")
                .addSink("stocks-sink", "stock-performance", stringSerializer, stockPerformanceSerializer, "stocks-processor")
                .addProcessor("stocks-printer", Logger::new, "stocks-processor");

        log.info("{}", topology.describe());

        KafkaStreams kafkaStreams = new KafkaStreams(topology, streamsConfig);
        MockDataProducer.produceStockTransactionsWithKeyFunction(50,50, 25, StockTransaction::getSymbol);
        System.out.println("Stock Analysis App Started");
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(70000);
        System.out.println("Shutting down the Stock Analysis App now");
        kafkaStreams.close();
        MockDataProducer.shutdown();
    }


    static class Logger implements Processor<String, StockPerformance, String, StockPerformance> {

        @Override
        public void process(Record<String, StockPerformance> record) {
            Utils.log.info("{}: {}", record.key(), record.value());
        }
    }
}

