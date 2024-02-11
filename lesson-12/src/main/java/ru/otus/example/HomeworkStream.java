package ru.otus.example;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;
import ru.otus.utils.Utils;

import java.time.Duration;

public class HomeworkStream {
    public static void main(String[] args) throws Exception {
        var builder = new StreamsBuilder();
        Serde<String> stringSerde = Serdes.String();
        KTable<Windowed<String>, Long> countedStream = builder
                .stream("events", Consumed.with(stringSerde, stringSerde))
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(5)))
                .count();
        countedStream.toStream()
                .foreach((k, v) -> {
                    Utils.log.info("Window {}: {}", k, v);
                });
    }
}
