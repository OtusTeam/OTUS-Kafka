package ru.otus.example.ex13.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Controller {
    private final KafkaService service;

    @GetMapping("/count")
    public Model getCount(@RequestParam String industry) {
        try {
            return new Model(industry, service.count(industry), null);
        } catch (Exception e) {
            log.error("Exception", e);
            return new Model(industry, null, e.getMessage());
        }
    }
}
