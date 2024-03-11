import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;

public class Ex3Producer {

    public static void main(String[] args) {
        var producer = new KafkaProducer<Integer, String>(Utils.createProducerConfig(m ->
            m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class)
        ));

        for (int i = 0; i < 200; i++) {
            Utils.log.info("Send {}", i);

            var record = new ProducerRecord<>("topic1", i, Integer.toString(i));
            producer.send(record,
                (metadata, error) -> Utils.log.info("Complete {}", record.key()));

            if (i % 50 == 0) {
                Utils.log.info("===== FLUSH =====");
                producer.flush();
            }
        }

        producer.close();
    }

}
