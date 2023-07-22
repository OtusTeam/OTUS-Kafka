import org.apache.avro.AvroMissingFieldException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import ru.otus.AvroConsumerExample;
import ru.otus.AvroProducerExample;
import ru.otus.Course;
import ru.otus.kafka.Student;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        //Можно проверить данные с помощью kafkacat:
        //kcat -C -b localhost:19092 -t students -Z -K:
        AvroProducerExample.sendStudent("student-1", doSpecificMethod());

        AvroConsumerExample.readStudent();

        //doGenericMethod();
        //System.out.println(doSpecificMethod().toString());
        //doReflectMethod();

    }

    public static void doGenericMethod() {

        //1 - чтение схемы из файла student.avsc
        String studentSchema = null;

        try {
            byte[] bytes = Files.readAllBytes(Paths.get("src/main/avro/student.avsc"));
            studentSchema = new String(bytes);
        } catch (IOException e) {
            System.out.println(e);
        }

        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(<TODO>);

        //2 - Создание объекта Generic Record из схемы
        GenericRecord avroRecord = new GenericData.Record(schema);
        avroRecord.put(<TODO>);

        ArrayList<CharSequence> courses = new ArrayList<>();
        courses.add("Kafka");
        courses.add("Data Engineer");

        avroRecord.put(<TODO>);

        System.out.println(avroRecord);
    }



    public static Student doSpecificMethod() {

        //1 - использование автоматически сгенерированных классов из схемы student.avsc
        //Класс Student генерируется с помощью команды mvn avro:schema или mvn package
        //Результат находится в папке target и доступен в classpath

        ArrayList<CharSequence> courses = new ArrayList<>();
        courses.add("Kafka");
        courses.add("Data Engineer");

        //Создание объекта Student
        //Student реализует интерфейс SpecificRecord
        Student st1 = Student.newBuilder()
                <TODO>
                .build();

        return st1;

    }

    public static void doReflectMethod() {

        //1 - использование вручуню написанного класса
        Course c = <TODO>

        //2 - генерация Avro-схемы в формате JSON
        Schema schema = ReflectData.get().getSchema(Course.class);

        System.out.println(schema.toString(true));

    }


}
