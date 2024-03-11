package ru.otus.writer;

import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

public class SchemaGenerator {
    // генерация Avro-схемы в формате JSON
    public static void main(String[] args) {
        Schema schema = ReflectData.get().getSchema(Course.class);

        System.out.println(schema.toString(true));
    }
}
