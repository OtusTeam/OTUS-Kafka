package ru.example.kafka.consumer

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.IterableHasAsJava

object Consumer {
  def main(args: Array[String]): Unit = {
    // Параметры
    val servers = "localhost:9092"
    val topic   = "test"
    val group   = "g1"

    // Создаём Consumer и подписываемся на тему
    val props = new Properties()
    props.put("bootstrap.servers", servers)
    props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", group)
    props.put("auto.offset.reset", "earliest")
    props.put("enable.auto.commit", "false")
    val consumer = new KafkaConsumer(props)
    consumer.subscribe(List(topic).asJavaCollection)

    // Читаем тему
    try {
      while (true) {
        consumer
          .poll(Duration.ofSeconds(1))
          .forEach { msg => println(s"${msg.partition}\t${msg.offset}\t${msg.key}\t${msg.value}") }
      }
    } catch {
      case e: Exception =>
        println(e.getLocalizedMessage)
        sys.exit(-1)
    } finally {
      consumer.close()
    }

    sys.exit(0)
  }
}
