package ru.otus.spring.ex8.receivejson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
@EnableKafka
public class Ex8Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex8Application.class);
        sa.setAdditionalProfiles("ex8");
        sa.run(args);
    }
}

