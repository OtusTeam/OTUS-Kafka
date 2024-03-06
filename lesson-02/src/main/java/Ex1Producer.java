import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class Ex1Producer {

    public static void main(String[] args) {
        Utils.log.info("Hello");

        var producer = new KafkaProducer<String, String>(Utils.producerConfig);
        for (int i = 0; i < 2; i++) {
            producer.send(new ProducerRecord<>("topic1", Integer.toString(i), Integer.toString(i)));
        }

        producer.close();
    }

}
