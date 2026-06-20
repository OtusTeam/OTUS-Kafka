import java.util.Scanner;

import json.JsonSerializer;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;

@Data
@Accessors(chain = true)
class SomeModel {
    private int year;
    private String fio;
}

// сериализатор в json
public class Ex4Producer {

    public static void main(String[] args) {
        try (var producer = new KafkaProducer<String, SomeModel>(Utils.createProducerConfig(map -> {
            map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        }))) {

            producer.send(new ProducerRecord<>("topic1", "m1", new SomeModel().setYear(1999).setFio("Ivanov")));
            producer.send(new ProducerRecord<>("topic1", "m2", new SomeModel().setYear(2005).setFio("Petrov")));

        }
    }

}
