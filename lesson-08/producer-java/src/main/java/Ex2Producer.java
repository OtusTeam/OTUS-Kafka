import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;

import java.util.concurrent.CompletableFuture;

// send с callback-ом
public class Ex2Producer {

    public static void main(String[] args) throws Exception {
        var producer = new KafkaProducer<Integer, String>(Utils.createProducerConfig(m -> {
                    m.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
                    m.put(ProducerConfig.LINGER_MS_CONFIG, "500");
                }
        ));

        for (int i = 0; i < 200; i++) {
            Utils.log.info("Send {}", i);

            var record = new ProducerRecord<>("topic1", i, Integer.toString(i));
            producer.send(record,
                (metadata, error) -> Utils.log.info("Complete {}", record.key())); // обратите внимание, когда сообщение фактически отправляется
        }

        Utils.log.info("== sleep");
        Thread.sleep(2000);

        Utils.log.info("== close");
        producer.close();
    }

}
