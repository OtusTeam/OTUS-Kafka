package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducerExample {

    public void getKafkaExample() throws ExecutionException, InterruptedException {

        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9092");
        props.put("key.Serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.Serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        String k = "mykey";
        String v = "myvalue";
        ProducerRecord<String, String> record =
                new ProducerRecord<String, String>("myTopic", k, v);
      
        //Blocks until completed
        producer.send(record).get();
      
        //Async: callback with lambda expression
        producer.send(record, (recordMetadata, e) -> {
            if (e != null) {
                e.printStackTrace();
            } else {
                System.out.println("Message sent: " + record.key());
            }
        });
      
        //Async
        producer.send(record, new CustomCallback());
        
        //Wait for all previously sent messages, then close
        producer.close();
      
        //Wait for 60 seconds, then close
        producer.close(Duration.ofSeconds(60));

    }
}

