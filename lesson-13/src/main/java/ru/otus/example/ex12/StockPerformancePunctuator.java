package ru.otus.example.ex12;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Record;
import ru.otus.model.stock.StockPerformance;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.processor.api.ProcessorContext;

@RequiredArgsConstructor
@Slf4j
public class StockPerformancePunctuator implements Punctuator {


    private final double differentialThreshold;
    private final ProcessorContext<String, StockPerformance> context;
    private final KeyValueStore<String, StockPerformance> keyValueStore;

    @Override
    public void punctuate(long timestamp) {
        log.info("punctuate");
        try(KeyValueIterator<String, StockPerformance> performanceIterator = keyValueStore.all()) {
            while (performanceIterator.hasNext()) {
                KeyValue<String, StockPerformance> keyValue = performanceIterator.next();
                String key = keyValue.key;
                StockPerformance stockPerformance = keyValue.value;

                if (stockPerformance != null) {
                    if (stockPerformance.priceDifferential() >= differentialThreshold ||
                            stockPerformance.shareDifferential() >= differentialThreshold) {
                        context.forward(new Record<>(key, stockPerformance, System.currentTimeMillis()));
                       // log.info("Forward: {}", stockPerformance);
                    }
                }
            }
        }
    }
}
