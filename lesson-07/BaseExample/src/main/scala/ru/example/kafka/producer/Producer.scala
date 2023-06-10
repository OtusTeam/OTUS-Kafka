package ru.example.kafka.producer

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization._
import java.util.Properties

object Producer {
  def main(args: Array[String]): Unit = {
    // Параметры
    val servers = "localhost:9092"
    val topic   = "test"

    // Создаём Producer
    val props = new Properties()
    props.put("bootstrap.servers", servers)
    props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[Int, String](props)

    // Генерируем записи
    try {
      (1 to 1000).foreach { i =>
        producer.send(new ProducerRecord(topic, i, s"Message $i"))
      }
    } finally {
      producer.flush()
      producer.close()
    }

    sys.exit(0)
  }
}
