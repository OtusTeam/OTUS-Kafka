package ru.otus;

import ru.otus.PersonOuterClass.Person;
import ru.otus.PersonOuterClass.Persons;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class ProtobufWriter {
  private static void writeUsersTo(OutputStream outputStream) throws Exception {
    var u1 = Person.newBuilder()
        .setName("Ivan")
        .setFavoriteNumber(10)
        .setFavoriteColor("blue")
        .setEmail("a@b.ru")
        .build();
    var u2 = Person.newBuilder()
        .setName("Petr")
        .setFavoriteNumber(42)
        .setFavoriteColor("red")
        .setEmail("b@c.ru")
        .build();
    var users = Persons.newBuilder()
        .addPerson(u1).addPerson(u2)
        .build();
    users.writeTo(outputStream);
    outputStream.flush();
  }

  private static void readUsersFrom(InputStream inputStream) throws Exception {
    var users = Persons.parseFrom(inputStream);
    var u1 = users.getPerson(0);
    var u2 = users.getPerson(1);
    System.out.println(u1);
    System.out.println(u2);
  }

  public static void main(String[] args) throws Exception {
    var byteArrayOutputStream = new ByteArrayOutputStream();
    writeUsersTo(byteArrayOutputStream);

    var bytes = byteArrayOutputStream.toByteArray();
    System.out.println(bytes.length);
    System.out.println(Arrays.toString(bytes));

    readUsersFrom(new ByteArrayInputStream(bytes));

    var fileOutputStream = new FileOutputStream("users-protobuf.data");
    writeUsersTo(fileOutputStream);
  }
}
