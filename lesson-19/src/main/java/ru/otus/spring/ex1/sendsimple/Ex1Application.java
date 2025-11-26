package ru.otus.spring.ex1.sendsimple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
public class Ex1Application {
    public static void main(String[] args) {
        RemoveAll.removeAll();
        SpringApplication.run(Ex1Application.class, args);
    }

}

