package ru.vzaigrin.example.kafka.java;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.*;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class Consumer {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: Consumer topics group brokers offset");
            System.out.println("offset: 'earliest' or 'latest'");
            System.exit(-1);
        }

        // Параметры
        String brokers = args[2];
        String[] topics = args[0].split(",");
        String group   = args[1];
        String offsetConfig = args[3];

        // Создаём Consumer и подписываемся на тему
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(GROUP_ID_CONFIG, group);
        props.put(AUTO_OFFSET_RESET_CONFIG, offsetConfig);

        KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);

        // Ссылка на главный поток
        final Thread mainThread = Thread.currentThread();

        // Регистрируем Shutdown Hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Detected a shutdown, let's exit by calling consumer.wakeup()...");
            consumer.wakeup();

            // join the main thread to allow the execution of the code in the main thread
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));

        try  {
            consumer.subscribe(Arrays.asList(topics));
            while (true) {
                ConsumerRecords<Integer, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<Integer, String> record : records) {
                    String topic = record.topic();
                    Integer partition = record.partition();
                    Long offset = record.offset();
                    Integer key = record.key();
                    String value = record.value();
                    System.out.printf("%s\t%d\t%d\t%d\t%s\n", topic, partition, offset, key, value);
                }
            }
        } catch (WakeupException e) {
            System.out.println("Consumer is starting to shut down...");
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(-1);
        } finally {
            // close the consumer and commit the offsets
            consumer.close(Duration.ofSeconds(10));
            System.out.println("The consumer is now gracefully shut down");
            System.exit(0);
        }

        System.exit(0);
    }
}
