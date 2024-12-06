# Iris ML Streaming

Пример использования Spark ML вместе со Spark Structured Streaming.
Приложение читает из входного потока Kafka строки типа CSV с данными из набора данных [Ирисы Фишера](https://ru.wikipedia.org/wiki/%D0%98%D1%80%D0%B8%D1%81%D1%8B_%D0%A4%D0%B8%D1%88%D0%B5%D1%80%D0%B0) и записывает их в выходной поток вместе с результатом классификации.
Параметры приложения находятся в файле *application.conf*

## Запуск

* Запускаем Kafka
* Создаем темы *input* и *prediction*
* В первом терминале запускаем *kafka-console-consumer.sh --topic prediction --bootstrap-server localhost:9092*
* Во втором терминале запускаем *spark-submit target/scala-2.12/IrisMLStructuredStreaming-assembly-1.0.jar*
* В третьем терминале запускаем *awk -F ',' 'NR > 1 { print $1 "," $2 "," $3 "," $4 }' < data/iris.csv | kafka-console-producer.sh --topic input --bootstrap-server localhost:9092*
