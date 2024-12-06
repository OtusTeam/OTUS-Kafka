# ML Structured Streaming

Пример использования Spark ML вместе с Structured Streaming.
Приложение читает из входного потока Kafka строки типа CSV с исходными данными в формате набора данных [Credit Card customers](https://www.kaggle.com/sakshigoyal7/credit-card-customers)
И записывает в выходной поток ID клиента и результат расчёта модели.

## Запуск

* Запускаем Kafka
* Создаем темы *input* и *prediction*
* В первом терминале запускаем *kafka-console-consumer.sh --topic prediction --bootstrap-server localhost:9092*
* Во втором терминале запускаем *spark-submit MLStructuredStreaming-assembly-1.0.jar*
* В третьем терминале запускаем *awk -F ',' 'NR > 1 { print $0 }' < BankChurners.csv | kafka-console-producer.sh --topic input --bootstrap-server localhost:9092*
