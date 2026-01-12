package ru.vzaigrin.example.kafka.java;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.*;
import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

public class ConsumerPartitionAssignment {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: ConsumerPartitionAssignment strategy brokers");
            System.out.println("Strategy:");
            System.out.println("\tRangeAssignor");
            System.out.println("\tRoundRobinAssignor");
            System.out.println("\tStickyAssignor");
            System.out.println("\tCooperativeStickyAssignor");
            System.exit(-1);
        }

        // Параметры
        String brokers = args[1];
        String topic1 = "t1";
        String topic2 = "t2";
        String group = "g1";
        String partitionAssignment = "org.apache.kafka.clients.consumer." + args[0];

        // Создаём Consumer и подписываемся на темы
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(GROUP_ID_CONFIG, group);
        props.put(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, partitionAssignment);

        KafkaConsumer<Integer, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic1, topic2), new MyConsumerRebalanceListener());

        // Получаем информацию о темах и разделах и выводим её на экран
        consumer.poll(Duration.ofSeconds(1));

        System.out.println("\nAfter subscribe");
        Set<TopicPartition> tpset = consumer.assignment();
        for (TopicPartition tp : tpset) {
            System.out.println("Topic: " + tp.topic() + ", Partition: " + tp.partition());
        }

        // Читаем темы
        try  {
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
