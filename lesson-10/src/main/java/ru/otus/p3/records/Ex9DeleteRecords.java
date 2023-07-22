package ru.otus.p3.records;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import ru.otus.RemoveAll;
import ru.otus.Utils;

import java.util.List;
import java.util.Map;

public class Ex9DeleteRecords {

    public static void main(String[] args) {
        Utils.doAdminAction(client -> {
            RemoveAll.removeAll(client);

            client.createTopics(List.of(new NewTopic("ex9", 2, (short)1))).all().get();

            Utils.sendMessages(0, 500, "ex9", 0);

            client.deleteRecords(Map.of(
                    new TopicPartition("ex9", 0), RecordsToDelete.beforeOffset(250)))
                            .all().get();
        });
    }
}
