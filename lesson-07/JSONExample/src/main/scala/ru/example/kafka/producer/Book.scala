package ru.example.kafka.producer

import org.apache.commons.csv.CSVRecord

case class Book(
    name: String,
    author: String,
    userRating: Float,
    reviews: Long,
    price: Int,
    year: Int,
    genre: String
)

object Book {
  def apply(r: CSVRecord): Book =
    Book(
      r.get("Name"),
      r.get("Author"),
      r.get("User Rating").toFloat,
      r.get("Reviews").toLong,
      r.get("Price").toInt,
      r.get("Year").toInt,
      r.get("Genre")
    )
}
