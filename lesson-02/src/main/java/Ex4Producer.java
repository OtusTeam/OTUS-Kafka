import java.util.Scanner;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;

public class Ex4Producer {

    public static void main(String[] args) {
        var producer = new KafkaProducer<String, String>(Utils.producerConfig);

        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter topic, keyFrom, keyTo and count");

            var topic = scanner.next();
            var keyFrom = scanner.nextInt();
            var keyTo = scanner.nextInt();
            var count = scanner.nextInt();

            for (int key = keyFrom; key < keyTo; key++) {
                for (int i = 0; i < count; i++) {
                    producer.send(new ProducerRecord<>(topic, Integer.toString(key), "some data"));
                }
            }
            producer.flush();
        }
    }

}
