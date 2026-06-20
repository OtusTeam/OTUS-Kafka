import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    public static Logger log = LoggerFactory.getLogger("appl");

    public static final String HOST = "localhost:9091";

    public static final Map<String, Object> producerConfig = Map.of(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

    public static Map<String, Object> createProducerConfig(Consumer<Map<String, Object>> builder) {
        var map = new HashMap<>(producerConfig);
        builder.accept(map);
        return map;
    }

    @SneakyThrows
    public static void createTopic(String name, int partitions, int replicationFactor) {
        try (var client = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, HOST))) {
            client.createTopics(List.of(new NewTopic(name, partitions, (short)replicationFactor))).all().get();
        }
    }
}
