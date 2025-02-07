package ru.otus.spring.ex9.createtopic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class Config {
    private final KafkaAdmin kafkaAdmin;

    @Bean
    public NewTopic topic1() {
        return TopicBuilder.name("ex9-topic1")
                .partitions(10)
                .replicas(2)
                .compact()
                .build();
    }

    @Bean
    public NewTopic topic2() {
        return TopicBuilder.name("ex9-topic2")
                .partitions(5)
                .replicas(2)
                .build();
    }
}
