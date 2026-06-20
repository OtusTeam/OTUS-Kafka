package ru.otus.sparkmlstreaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.concat_ws
import org.apache.spark.sql.types._
import java.util.Properties

object MLStreaming {
  def main(args: Array[String]): Unit = {
    // Проверяем аргументы вызова
    if (args.length != 5) {
      System.err.println(
        "Usage: MLStreaming <path-to-model> <bootstrap-servers> <groupId> <input-topic> <prediction-topic>"
      )
      System.exit(-1)
    }
    val Array(path2model, brokers, groupId, inputTopic, predictionTopic) = args

    // Создаём Streaming Context и получаем Spark Context
    val sparkConf        = new SparkConf().setAppName("MLStreaming")
    val streamingContext = new StreamingContext(sparkConf, Seconds(1))

    // Загружаем модель
    val model = PipelineModel.load(path2model)

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

    // Разбиваем входную строку на элементы
    val lines = messages
      .map(_.value)
      .map(_.replace("\"", "").split(","))

    // Обрабатываем каждый входной набор
    lines.foreachRDD { rdd =>
      if (!rdd.isEmpty) {
        val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
        import spark.implicits._

        // Преобразовываем RDD в DataFrame
        val data = rdd
          .toDF("input")
          .withColumn("CLIENTNUM", $"input" (0).cast(IntegerType))
          .withColumn("Attrition_Flag", $"input" (1).cast(StringType))
          .withColumn("Customer_Age", $"input" (2).cast(IntegerType))
          .withColumn("Gender", $"input" (3).cast(StringType))
          .withColumn("Dependent_count", $"input" (4).cast(IntegerType))
          .withColumn("Education_Level", $"input" (5).cast(StringType))
          .withColumn("Marital_Status", $"input" (6).cast(StringType))
          .withColumn("Income_Category", $"input" (7).cast(StringType))
          .withColumn("Card_Category", $"input" (8).cast(StringType))
          .withColumn("Months_on_book", $"input" (9).cast(IntegerType))
          .withColumn("Total_Relationship_Count", $"input" (10).cast(IntegerType))
          .withColumn("Months_Inactive_12_mon", $"input" (11).cast(IntegerType))
          .withColumn("Contacts_Count_12_mon", $"input" (12).cast(IntegerType))
          .withColumn("Credit_Limit", $"input" (13).cast(DoubleType))
          .withColumn("Total_Revolving_Bal", $"input" (14).cast(IntegerType))
          .withColumn("Avg_Open_To_Buy", $"input" (15).cast(DoubleType))
          .withColumn("Total_Amt_Chng_Q4_Q1", $"input" (16).cast(DoubleType))
          .withColumn("Total_Trans_Amt", $"input" (17).cast(IntegerType))
          .withColumn("Total_Trans_Ct", $"input" (18).cast(IntegerType))
          .withColumn("Total_Ct_Chng_Q4_Q1", $"input" (19).cast(DoubleType))
          .withColumn("Avg_Utilization_Ratio", $"input" (20).cast(DoubleType))
          .withColumn(
            "Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependent_count_Education_Level_Months_Inactive_12_mon_1",
            $"input" (21).cast(DoubleType)
          )
          .withColumn(
            "Naive_Bayes_Classifier_Attrition_Flag_Card_Category_Contacts_Count_12_mon_Dependent_count_Education_Level_Months_Inactive_12_mon_2",
            $"input" (22).cast(DoubleType)
          )
          .drop("input")

        val prediction = model.transform(data)

        prediction
          .withColumn("result", concat_ws(",", $"CLIENTNUM", $"prediction"))
          .select("result")
          .rdd
          .foreachPartition { partition =>
            val producer = new KafkaProducer(props, new StringSerializer, new StringSerializer)
            partition.foreach { record =>
              producer.send(new ProducerRecord(predictionTopic, record.getString(0)))
            }
            producer.close()
          }
      }
    }

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}
