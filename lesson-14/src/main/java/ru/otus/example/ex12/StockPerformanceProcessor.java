package ru.otus.example.ex12;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import ru.otus.model.stock.StockPerformance;
import ru.otus.model.stock.StockTransaction;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class StockPerformanceProcessor implements Processor<String, StockTransaction, String, StockPerformance> {

    private KeyValueStore<String, StockPerformance> keyValueStore;
    private final String stateStoreName;
    private final double differentialThreshold;

    @Override
    public void init(ProcessorContext<String, StockPerformance> context) {
        log.info("INIT");
        keyValueStore = context.getStateStore(stateStoreName);
        StockPerformancePunctuator punctuator = new StockPerformancePunctuator(differentialThreshold,
                context,
                keyValueStore);

        context.schedule(Duration.ofSeconds(10), PunctuationType.WALL_CLOCK_TIME, punctuator);
    }

    @Override
    public void process(Record<String, StockTransaction> record) {
        try {
            var symbol = record.key();
            var transaction = record.value();

            //log.info("Process {}: {}", symbol, transaction);
            if (symbol != null) {
                var stockPerformanceBuilder = Optional.ofNullable(keyValueStore.get(symbol))
                        .map(StockPerformance::toBuilder)
                        .orElseGet(() -> StockPerformance.builder().build().toBuilder());

                var stockPerformance = stockPerformanceBuilder
                        .updatePriceStats(transaction.getSharePrice())
                        .updateVolumeStats(transaction.getShares())
                        .lastUpdateSent(Instant.now().toEpochMilli())
                        .build();

                //log.info("{}", stockPerformance);

                keyValueStore.put(symbol, stockPerformance);
            }
        } catch (Exception e) {
            log.error("Error", e);
            throw e;
        }
    }

}
