package ru.otus.spring.ex4.sendtransaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
public class Ex4Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex4Application.class);
        sa.setAdditionalProfiles("ex4");
        sa.run(args);
    }
}

