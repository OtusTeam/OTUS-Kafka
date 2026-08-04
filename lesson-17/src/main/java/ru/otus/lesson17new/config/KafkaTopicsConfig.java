package ru.otus.lesson17new.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import ru.otus.lesson17new.common.Topics;

@Configuration
public class KafkaTopicsConfig {

    @Bean
    NewTopic orderCreatedTopic() {
        return topic(Topics.ORDER_CREATED, 2);
    }

    @Bean
    NewTopic paymentAuthorizedTopic() {
        return topic(Topics.PAYMENT_AUTHORIZED, 2);
    }

    @Bean
    NewTopic paymentRejectedTopic() {
        return topic(Topics.PAYMENT_REJECTED, 2);
    }

    @Bean
    NewTopic inventoryReservedTopic() {
        return topic(Topics.INVENTORY_RESERVED, 2);
    }

    @Bean
    NewTopic inventoryRejectedTopic() {
        return topic(Topics.INVENTORY_REJECTED, 2);
    }

    @Bean
    NewTopic orderConfirmedTopic() {
        return topic(Topics.ORDER_CONFIRMED, 2);
    }

    @Bean
    NewTopic orderCancelledTopic() {
        return topic(Topics.ORDER_CANCELLED, 2);
    }

    @Bean
    NewTopic orderHighValueTopic() {
        return topic(Topics.ORDER_HIGH_VALUE, 2);
    }

    @Bean
    NewTopic orderRegularTopic() {
        return topic(Topics.ORDER_REGULAR, 2);
    }

    @Bean
    NewTopic orderCreatedDltTopic() {
        return topic(Topics.ORDER_CREATED_DLT, 2);
    }

    private NewTopic topic(String name, int partitions) {
        return TopicBuilder.name(name)
                .partitions(partitions)
                .replicas(1)
                .build();
    }
}
