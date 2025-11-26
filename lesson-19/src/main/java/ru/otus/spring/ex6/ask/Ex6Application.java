package ru.otus.spring.ex6.ask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
@EnableKafka
public class Ex6Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex6Application.class);
        sa.setAdditionalProfiles("ex6");
        sa.run(args);
    }
}

