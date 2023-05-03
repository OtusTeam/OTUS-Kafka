import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Ex4Consumer {

    public static void main(String[] args) {
        var groupId = args.length > 0 ? args[0] : "java-app";

        var topics = args.length > 1 ?
            new ArrayList<>(List.of(args).subList(1, args.length))
            : List.of("topic1");

        Utils.log.warn("Subscribe to {} with {}", topics, groupId);

        var consumer = new KafkaConsumer<String, String>(Utils.createConsumerConfig(m ->
            m.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)));

        consumer.subscribe(topics);

        while (true) {
            var result = consumer.poll(Duration.ofSeconds(10));

            Utils.log.info("Read {}", result.count());

            for (var record : result) {
                Utils.log.warn("Message {}.{}: {} -> {}", record.topic(), record.partition(),
                    record.key(), record.value());
            }
        }
    }

}
