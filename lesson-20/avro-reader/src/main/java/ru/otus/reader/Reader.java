package ru.otus.reader;

import org.apache.avro.Schema;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import ru.otus.model.User;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class Reader {
    private static void readUsersFrom(InputStream inputStream) throws  Exception {
        var userDatumReader = new SpecificDatumReader<>(User.class);
        var decoder = DecoderFactory.get().binaryDecoder(inputStream, null);

        var u1 = userDatumReader.read(null, decoder);
        var u2 = userDatumReader.read(null, decoder);

        System.out.println(u1);
        System.out.println(u2);
    }

    private static void readUsersFrom2(InputStream inputStream) throws  Exception {
        var readerSchema = User.getClassSchema();
        var writerSchema = new Schema.Parser().parse(new File("avro-writer/src/main/avro/user.avsc"));

        var userDatumReader = new SpecificDatumReader<User>(writerSchema, readerSchema);
        var decoder = DecoderFactory.get().binaryDecoder(inputStream, null);

        var u1 = userDatumReader.read(null, decoder);
        var u2 = userDatumReader.read(null, decoder);

        System.out.println(u1);
        System.out.println(u2);
    }

    public static void main(String[] args)  throws Exception {
        readUsersFrom(new FileInputStream("users.data"));
    }
}
