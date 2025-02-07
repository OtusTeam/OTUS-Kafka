package ru.otus.sparkmlstreaming

import org.apache.spark.internal.Logging

case class Iris(
    sepal_length: Double,
    sepal_width: Double,
    petal_length: Double,
    petal_width: Double
)

object Iris extends Logging {
  def apply(a: Array[String]): Iris = {
    try {
      Iris(a(0).toDouble, a(1).toDouble, a(2).toDouble, a(3).toDouble)
    } catch {
      case e: Throwable =>
        logInfo(s"Error: ${e.getLocalizedMessage}")
        Iris(0, 0, 0, 0)
    }
  }
}
