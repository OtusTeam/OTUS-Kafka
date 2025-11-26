package ru.otus.spring.ex7.exactlyonce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
@EnableKafka
public class Ex7Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex7Application.class);
        sa.setAdditionalProfiles("ex7");
        sa.run(args);
    }
}

