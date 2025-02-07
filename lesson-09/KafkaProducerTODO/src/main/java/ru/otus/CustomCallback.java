package ru.otus;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class CustomCallback implements Callback {

    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
        if (e != null) {
            TODO
        } else {
            TODO
        }
    }
}
