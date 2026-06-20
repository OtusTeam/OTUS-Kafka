import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import partitioner.MyPartitioner;


// разные acks
public class Ex6Producer {
    private static final String LONG_STRING;

    static {
        var sb = new StringBuilder();
        while (sb.length() < 1024*10)
            sb.append("some data ");
        LONG_STRING = sb.toString();
    }

    public static void main(String[] args) {
        //Utils.createTopic("topic3", 2, 3);

        sendMany("0");
        sendMany("1");
        sendMany("all");
    }

    private static void sendMany(String acks) {
        var ticks = System.currentTimeMillis();

        try (var producer = new KafkaProducer<String, String>(Utils.createProducerConfig(map ->
                map.put(ProducerConfig.ACKS_CONFIG, acks)))) {

            for (int i = 0; i < 100000; i++) {
                producer.send(new ProducerRecord<>("topic2", Integer.toString(i), LONG_STRING));
            }
        }

        Utils.log.info("===== send complete {}: {}", acks, (System.currentTimeMillis() - ticks));
    }

}
