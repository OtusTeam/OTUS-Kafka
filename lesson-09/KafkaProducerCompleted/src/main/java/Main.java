
import ru.otus.KafkaProducerExample;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //Можно проверить данные с помощью kafkacat:
        //kcat -C -b localhost:19092 -t myTopic -Z -K:

        KafkaProducerExample.getKafkaExample();


    }


}
