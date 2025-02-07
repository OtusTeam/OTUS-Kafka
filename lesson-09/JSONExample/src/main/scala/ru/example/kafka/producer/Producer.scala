package ru.example.kafka.producer

import com.typesafe.config.ConfigFactory
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax.EncoderOps
import org.apache.commons.csv.CSVFormat
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import java.io.FileReader
import java.util.Properties

object Producer {
  def main(args: Array[String]): Unit = {

    // Читаем конфигурационный файл
    val config = ConfigFactory.load()

    // Создаём Producer
    val props = new Properties()
    props.put("bootstrap.servers", config.getString("bootstrap.servers"))
    val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)
    val topic    = config.getString("topic")

    // Читаем файл с данными
    val in = new FileReader(config.getString("input"))
    val csvFormat =
      CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build()
    val records = csvFormat.parse(in)

    // Encoder для Book
    implicit val bookEncoder: Encoder[Book] = deriveEncoder[Book]

    // Преобразовываем записи в JSON и отправляем в Kafka
    try {
      records.forEach { r =>
        val b = Book(r).asJson.noSpaces
        producer.send(new ProducerRecord(topic, r.getRecordNumber.toString, b))
      }
    } catch {
      case e: Exception =>
        println(e.getLocalizedMessage)
        sys.exit(-1)
    } finally {
      records.close()
      producer.close()
    }

    sys.exit(0)
  }
}
