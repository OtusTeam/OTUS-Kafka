```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-producer --topic ex5-text --property "parse.key=true" --property "key.separator=:" --bootstrap-server kafka1:9191
```

```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-producer --topic ex5-text --bootstrap-server kafka1:9191
```

```json
  {"code": 42, "name": "Petrov"}
```