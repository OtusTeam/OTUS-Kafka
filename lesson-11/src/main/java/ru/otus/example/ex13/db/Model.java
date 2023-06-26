package ru.otus.example.ex13.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model {
    private String industry;
    private Long count;
    private String error;
}
