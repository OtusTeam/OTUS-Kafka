package ru.otus.spring.ex3.senddif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
public class Ex3Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        var sa = new SpringApplication(Ex3Application.class);
        sa.run(args);
    }
}

