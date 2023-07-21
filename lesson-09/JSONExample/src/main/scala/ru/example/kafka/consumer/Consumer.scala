package ru.example.kafka.consumer

import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.Properties
import scala.jdk.CollectionConverters.IterableHasAsJava

object Consumer {
  def main(args: Array[String]): Unit = {

    // Читаем конфигурационный файл
    val config             = ConfigFactory.load()
    val topic              = config.getString("topic")
    val maxMsgForPartition = config.getInt("maxMsgForPartition")

    // Создаём Consumer и подписываемся на тему
    val props = new Properties()
    props.put("bootstrap.servers", config.getString("bootstrap.servers"))
    props.put("group.id", config.getString("group.id"))
    val consumer = new KafkaConsumer(props, new StringDeserializer, new StringDeserializer)
    consumer.subscribe(List(topic).asJavaCollection)

    try {
      // Получаем секции
      consumer.poll(Duration.ofSeconds(1))
      val topicPartitions = consumer.assignment()

      // Смещаемся на 5 записей от конца
      consumer.seekToEnd(topicPartitions)
      topicPartitions.forEach { tp =>
        consumer.seek(tp, consumer.position(tp) - maxMsgForPartition)
      }

      // Читаем и выводим последние 5 записей из каждой секции
      (1 to topicPartitions.toArray.length).foreach { _ =>
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
