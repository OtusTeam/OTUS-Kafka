package ru.otus.example.ex13.db;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.utils.Utils;

@Service
@Slf4j
public class KafkaService {
    private int port;
    private KafkaStreams kafkaStreams;
    private ReadOnlyKeyValueStore<String, Long> store;
    private final Serde<String> stringSerde = Serdes.String();
    private final Serde<Long> longSerde = Serdes.Long();
    public static final String STORE = "count-store";

    @EventListener
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        port = event.getWebServer().getPort();

        var builder = new StreamsBuilder();

        builder
                .stream("ex13-topic", Consumed.with(stringSerde, stringSerde))
                .groupByKey()
                .count(Utils.materialized(STORE, stringSerde, longSerde));

        kafkaStreams = new KafkaStreams(builder.build(), Utils.createStreamsConfig(b -> {
            b.put(StreamsConfig.APPLICATION_ID_CONFIG, "ex13");
            b.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:" + port);
            b.put(StreamsConfig.STATE_DIR_CONFIG, System.getProperty("user.dir") + "/kafka-state-" + port);
        }));

        kafkaStreams.start();

        store = kafkaStreams.store(StoreQueryParameters
                .fromNameAndType(STORE, QueryableStoreTypes.keyValueStore()));
    }

    @PreDestroy
    public void closeStreams() {
        kafkaStreams.close();
    }

    public Long count(String industry) {
        var host = kafkaStreams.queryMetadataForKey(STORE, industry, stringSerde.serializer()).activeHost();
        /*if (host.host().equals("unavailable")) {
            log.info("Unavailable store");
            return -1L;
        }*/
        if (host.port() == port || host.host().equals("unavailable")) {
            log.info("Query to local store");
            return store.get(industry);
        }

        return queryRemote(industry, host);
    }

    private Long queryRemote(String industry, HostInfo hostInfo) {
        log.info("Query to remote store {}", hostInfo);

        var restTemplate = new RestTemplate();

        return restTemplate.getForObject("http://{host}:{port}/count?industry={industry}", Model.class,
                hostInfo.host(),
                hostInfo.port(),
                industry).getCount();
    }
}
