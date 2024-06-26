#### ЧТЕНИЕ И ЗАПИСЬ В ФОРМАТЕ STRING

# Записать сообщение в формате JSON в топик jsontest
```sh
curl -X POST -H "Content-Type: application/vnd.kafka.json.v2+json" \
      --data '{"records":[{"value":{"foo":"bar"}}]}' "http://localhost:8082/topics/jsontest"
```      

# Создать Consumer JSON в группе "my_json_consumer_group", начать читать с начала топика.

```sh
curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" \
      --data '{"name": "my_consumer_instance", "format": "json", "auto.offset.reset": "earliest"}' \
      http://localhost:8082/consumers/my_json_consumer_group
```

# Подписаться на топик
```sh
curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" --data '{"topics":["jsontest"]}' \
 http://localhost:8082/consumers/my_json_consumer_group/instances/my_consumer_instance/subscription
```

# Прочитать данные
```sh
curl -X GET -H "Accept: application/vnd.kafka.json.v2+json" \
      http://localhost:8082/consumers/my_json_consumer_group/instances/my_consumer_instance/records
```

# Удалить Consumer, чтобы не занимать ресурсы

```sh
curl -X DELETE -H "Content-Type: application/vnd.kafka.v2+json" \
      http://localhost:8082/consumers/my_json_consumer_group/instances/my_consumer_instance
```

#### ЧТЕНИЕ И ЗАПИСЬ В ФОРМАТЕ AVRO

# Записать сообщение в формате Avro вместе со схемой
```sh
curl -X POST -H "Content-Type: application/vnd.kafka.avro.v2+json" \
      -H "Accept: application/vnd.kafka.v2+json" \
      --data '{"value_schema": "{\"type\": \"record\", \"name\": \"User\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}]}", "records": [{"value": {"name": "testUser"}}]}' \
      "http://localhost:8082/topics/avrotest"
```

# Записать сообщение вместе с ключом в формате Avro (и ключ, и сообщение должны иметь одинаковый формат)
```sh
curl -X POST -H "Content-Type: application/vnd.kafka.avro.v2+json" \
      -H "Accept: application/vnd.kafka.v2+json" \
      --data '{"key_schema": "{\"name\":\"user_id\"  ,\"type\": \"int\"   }", "value_schema": "{\"type\": \"record\", \"name\": \"User\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}]}", "records": [{"key" : 1 , "value": {"name": "testUser"}}]}' \
      "http://localhost:8082/topics/avrokeytest2"
```

# Создать Consumer Avro
```sh
curl -X POST  -H "Content-Type: application/vnd.kafka.v2+json" \
      --data '{"name": "my_consumer_instance", "format": "avro", "auto.offset.reset": "earliest"}' \
      http://localhost:8082/consumers/my_avro_consumer_group
```

# Подписаться на топик
```sh
curl -X POST -H "Content-Type: application/vnd.kafka.v2+json" --data '{"topics":["avrotest"]}' \
      http://localhost:8082/consumers/my_avro_consumer_group/instances/my_consumer_instance/subscription
```

# Прочитать данные в формате Avro. Схема подгрузится из SchemaRegistry автоматически
```sh
curl -X GET -H "Accept: application/vnd.kafka.avro.v2+json" \
      http://localhost:8082/consumers/my_avro_consumer_group/instances/my_consumer_instance/records
```

# Удалить Consumer, чтобы не занимать ресурсы
```sh
curl -X DELETE -H "Content-Type: application/vnd.kafka.v2+json" \
      http://localhost:8082/consumers/my_avro_consumer_group/instances/my_consumer_instance
```

#### МОНИТОРИНГ

# Список топиков
```sh
curl "http://localhost:8082/topics"
```

# Информация о топике
```sh
curl "http://localhost:8082/topics/avrotest"
```

# Информация о партициях топика
```sh
curl "http://localhost:8082/topics/avrotest/partitions"
```
