package ru.otus.sparkmlstreaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.mllib.tree.model.RandomForestModel
import java.util.Properties

object IrisMLStreaming {
  def main(args: Array[String]): Unit = {
    // Проверяем аргументы вызова
    if (args.length != 5) {
      System.err.println(
        "Usage: IrisMLStreaming <path-to-model> <bootstrap-servers> <groupId> <input-topic> <prediction-topic>"
      )
      System.exit(-1)
    }
    val Array(path2model, brokers, groupId, inputTopic, predictionTopic) = args

    // Создаём Streaming Context и получаем Spark Context
    val sparkConf        = new SparkConf().setAppName("IrisMLStreaming")
    val streamingContext = new StreamingContext(sparkConf, Seconds(1))
    val sparkContext     = streamingContext.sparkContext

    // Загружаем модель
    val model = RandomForestModel.load(sparkContext, path2model)

    // Границы значений признаков
    val sepalLengthMin = 4.3
    val sepalLengthMax = 7.9
    val sepalWidthMin  = 2
    val sepalWidthMax  = 4.4
    val petalLengthMin = 1
    val petalLengthMax = 6.9
    val petalWidthMin  = 0.1
    val petalWidthMax  = 2.5

    // Названия классов
    val names = Map(0.0 -> "setosa", 1.0 -> "versicolor", 2.0 -> "virginica")

    // Создаём свойства Producer'а для вывода в выходную тему Kafka (тема с расчётом)
    val props: Properties = new Properties()
    props.put("bootstrap.servers", brokers)

    // Параметры подключения к Kafka для чтения
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG        -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG                 -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG   -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
    )

    // Подписываемся на входную тему Kafka (тема с данными)
    val inputTopicSet = Set(inputTopic)
    val messages = KafkaUtils.createDirectStream[String, String](
      streamingContext,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](inputTopicSet, kafkaParams)
    )

    val iris = messages
      .map(_.value)
      // Разбиваем входную строку на элементы
      .map(_.split(","))
      // Проверяем, что получили 4 числа
      .filter(a => isDouble(a(0)) && isDouble(a(1)) && isDouble(a(2)) && isDouble(a(3)))
      // Преобразовываем в числа
      .map { record => record.slice(0, 4).map(_.toDouble) }
      // Проверяем значения на вхождение в диапазон
      .filter { a =>
        a(0) >= sepalLengthMin && a(0) <= sepalLengthMax &&
        a(1) >= sepalWidthMin && a(1) <= sepalWidthMax &&
        a(2) >= petalLengthMin && a(2) <= petalLengthMax &&
        a(3) >= petalWidthMin && a(3) <= petalWidthMax
      }
      // Преобразовываем в вектор
      .map(Vectors.dense)

    // Обрабатываем каждый входной набор
    iris.foreachRDD { rdd =>
      if (!rdd.isEmpty) {
        val prediction = model.predict(rdd).map(names)
        rdd.zip(prediction).foreachPartition { partition =>
          val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)
          partition.foreach { record =>
            val vector = record._1.toArray.mkString(",")
            producer.send(new ProducerRecord(predictionTopic, s"$vector,${record._2}"))
          }
          producer.close()
        }
      }
    }

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  // Функция проверяет, что строка является числом
  def isDouble(s: String): Boolean = {
    val regex = "^[0-9]+(\\.[0-9]+)?$".r
    regex.findFirstIn(s).isDefined
  }
}
