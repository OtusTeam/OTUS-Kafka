package ru.otus.lesson17new.streams;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.otus.lesson17new.common.Topics;
import ru.otus.lesson17new.events.OrderCreatedEvent;

@Configuration
@EnableKafkaStreams
@Profile("streams")
public class EventRouterStreamsConfig {
    private static final BigDecimal HIGH_VALUE_THRESHOLD = BigDecimal.valueOf(1_000);

    @Bean
    KStream<String, OrderCreatedEvent> routeOrders(StreamsBuilder builder, ObjectMapper objectMapper) {
        var orderSerde = orderSerde(objectMapper);
        KStream<String, OrderCreatedEvent> orders = builder.stream(
                Topics.ORDER_CREATED,
                Consumed.with(Serdes.String(), orderSerde)
        );

        orders.split(Named.as("order-value-"))
                .branch(
                        (orderId, event) -> event.total().compareTo(HIGH_VALUE_THRESHOLD) >= 0,
                        Branched.withConsumer(stream -> stream.to(
                                Topics.ORDER_HIGH_VALUE,
                                Produced.with(Serdes.String(), orderSerde)
                        ))
                )
                .defaultBranch(Branched.withConsumer(stream -> stream.to(
                        Topics.ORDER_REGULAR,
                        Produced.with(Serdes.String(), orderSerde)
                )));

        return orders;
    }

    private Serde<OrderCreatedEvent> orderSerde(ObjectMapper objectMapper) {
        var serializer = new JsonSerializer<OrderCreatedEvent>(objectMapper);
        var deserializer = new JsonDeserializer<>(OrderCreatedEvent.class, objectMapper, false);
        deserializer.addTrustedPackages("ru.otus.lesson-17.events");
        return Serdes.serdeFrom(serializer, deserializer);
    }
}
