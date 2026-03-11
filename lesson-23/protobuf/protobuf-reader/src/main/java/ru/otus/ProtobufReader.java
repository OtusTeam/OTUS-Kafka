package ru.otus;

import ru.otus.PersonOuterClass.Persons;
import java.io.FileInputStream;
import java.io.InputStream;

public class ProtobufReader {
  private static void readUsersFrom(InputStream inputStream) throws Exception {
    var users = Persons.parseFrom(inputStream);
    var u1 = users.getPerson(0);
    var u2 = users.getPerson(1);
    System.out.println(u1);
    System.out.println(u2);
  }

  public static void main(String[] args) throws Exception {
    readUsersFrom(new FileInputStream("users-protobuf.data"));
  }
}
