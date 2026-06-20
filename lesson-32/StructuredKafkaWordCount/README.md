# Structured Kafka Word Count

Пример простого приложения Spark Streaming, которое читает данные из Kafka

## Запуск

* Запускаем Kafka
* Создаем тему *words*
* В первом терминале запускаем *usr/local/kafka/bin/kafka-console-producer.sh --topic words --bootstrap-server localhost:9092* и вводим слова, разделённые пробелом
* Во втором терминале запускаем *spark-submit StructuredKafkaWordCount-assembly-1.0.jar localhost:9092 subscribe words*
