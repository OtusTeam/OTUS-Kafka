package ru.vzaigrin.example.kafka.java;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import java.util.Collection;

public class MyConsumerRebalanceListener implements ConsumerRebalanceListener {

    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        System.out.println("\nPartitions Revoked");
        for (TopicPartition tp : partitions) {
            System.out.println("Topic: " + tp.topic() + ", Partition: " + tp.partition());
        }
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        System.out.println("\nPartitions Assigned");
        for (TopicPartition tp : partitions) {
            System.out.println("Topic: " + tp.topic() + ", Partition: " + tp.partition());
        }
    }
}
