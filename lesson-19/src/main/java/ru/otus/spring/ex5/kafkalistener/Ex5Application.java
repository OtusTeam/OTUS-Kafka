package ru.otus.spring.ex5.kafkalistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
@EnableKafka // см
public class Ex5Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex5Application.class);
        sa.run(args);
    }
}

