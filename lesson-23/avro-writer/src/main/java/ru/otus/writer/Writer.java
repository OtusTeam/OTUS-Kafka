package ru.otus.writer;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import ru.otus.model.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Writer {
    private static void writeUsersTo(OutputStream outputStream) throws Exception {
        var u1 = new User(
                "Ivan",
                // /*1*/ "a@b.ru",
                10,
                "blue");
        var u2 = User.newBuilder()
                .setName("Petr")
                 ///*1*/ .setEmail("b@c.ru")
                .setFavoriteColor("red")
                .setFavoriteNumber(42)
                .build();

        var userDatumWriter = new SpecificDatumWriter<>(User.class);
        var encoder = EncoderFactory.get().binaryEncoder(outputStream, null);

        userDatumWriter.write(u1, encoder);
        userDatumWriter.write(u2, encoder);

        encoder.flush();
    }

    private static void readUsersFrom(InputStream inputStream) throws  Exception {
        var userDatumReader = new SpecificDatumReader<>(User.class);
        var decoder = DecoderFactory.get().binaryDecoder(inputStream, null);


        var u1 = userDatumReader.read(null, decoder);
        var u2 = userDatumReader.read(null, decoder);

        System.out.println(u1);
        System.out.println(u2);
    }

    public static void main(String[] args)  throws Exception {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        writeUsersTo(byteArrayOutputStream);

        var bytes = byteArrayOutputStream.toByteArray();
        System.out.println(Arrays.toString(bytes));

        readUsersFrom(new ByteArrayInputStream(bytes));

        var fileOutputStream = new FileOutputStream("users.data");
        writeUsersTo(fileOutputStream);
    }
}
