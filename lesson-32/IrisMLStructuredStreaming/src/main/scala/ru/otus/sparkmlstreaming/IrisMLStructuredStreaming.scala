package ru.otus.sparkmlstreaming

import com.typesafe.config.ConfigFactory
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object IrisMLStructuredStreaming {
  def main(args: Array[String]): Unit = {
    // Читаем конфигурационный файл
    val config                 = ConfigFactory.load()
    val inputBootstrapServers  = config.getString("input.bootstrap.servers")
    val inputTopic             = config.getString("input.topic")
    val outputBootstrapServers = config.getString("output.bootstrap.servers")
    val outputTopic            = config.getString("output.topic")
    val path2model             = config.getString("path2model")
    val checkpointLocation     = config.getString("checkpointLocation")

    // Загружаем модель
    val model = PipelineModel.load(path2model)

    // Границы значений признаков
    val sepalLengthMin = 4.3
    val sepalLengthMax = 7.9
    val sepalWidthMin  = 2
    val sepalWidthMax  = 4.4
    val petalLengthMin = 1
    val petalLengthMax = 6.9
    val petalWidthMin  = 0.1
    val petalWidthMax  = 2.5

    // Создаём SparkSession
    val spark = SparkSession.builder
      .appName("IrisMLStructuredStreaming")
      .getOrCreate()

    import spark.implicits._

    // Читаем входной поток
    val iris = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", inputBootstrapServers)
      .option("subscribe", inputTopic)
      .load()
      .selectExpr("CAST(value AS STRING)")
      .as[String]
      .map(_.split(","))
      .map(Iris(_))
      .filter(i => between(i.sepal_length, sepalLengthMin, sepalLengthMax))
      .filter(i => between(i.sepal_width, sepalWidthMin, sepalWidthMax))
      .filter(i => between(i.petal_length, petalLengthMin, petalLengthMax))
      .filter(i => between(i.petal_width, petalWidthMin, petalWidthMax))

    val prediction: DataFrame = model.transform(iris)

    // Выводим результат
    val query = prediction
      .select(
        concat_ws(",", $"sepal_length", $"sepal_width", $"petal_length", $"petal_width", $"predictedLabel").as("value")
      )
      .writeStream
      .option("checkpointLocation", checkpointLocation)
      .outputMode("append")
      .format("kafka")
      .option("kafka.bootstrap.servers", outputBootstrapServers)
      .option("topic", outputTopic)
      .start()

    query.awaitTermination()
  }

  def between[T](d: T, min: T, max: T)(implicit ev: T => Ordered[T]): Boolean = d >= min && d <= max
}
