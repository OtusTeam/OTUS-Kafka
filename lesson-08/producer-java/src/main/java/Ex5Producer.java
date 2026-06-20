import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import partitioner.MyPartitioner;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


// свой партишионер
public class Ex5Producer {

    public static void main(String[] args) {
        //Utils.createTopic("topic2", 2, 1);

        try (var producer = new KafkaProducer<String, String>(Utils.createProducerConfig(map ->
                map.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, MyPartitioner.class)))) {

            for (int i = 0; i < 10; i++) {
                producer.send(new ProducerRecord<>("topic2", Integer.toString(i), Integer.toString(i)));
            }
        }
    }

}
