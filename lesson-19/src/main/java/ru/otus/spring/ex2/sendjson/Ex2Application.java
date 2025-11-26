package ru.otus.spring.ex2.sendjson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
public class Ex2Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex2Application.class);
        sa.setAdditionalProfiles("ex2");
        sa.run(args);
    }
}

