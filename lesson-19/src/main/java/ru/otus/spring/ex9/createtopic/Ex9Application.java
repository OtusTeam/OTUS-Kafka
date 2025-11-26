package ru.otus.spring.ex9.createtopic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.otus.spring.util.RemoveAll;

@SpringBootApplication
public class Ex9Application {
    public static void main(String[] args) {
        //RemoveAll.removeAll();
        SpringApplication.run(Ex9Application.class, args);
    }

}

