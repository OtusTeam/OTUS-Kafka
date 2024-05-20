Запуск контейнера
```shell
cd kafka
docker compose start
```

Создать топик
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-topics --create --topic test --partitions 1 --replication-factor 1 --bootstrap-server kafka1:9191
```

Получить список топиков
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-topics --list --bootstrap-server kafka1:9191
```

Получить описание топика
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-topics --describe --topic test --bootstrap-server kafka1:9191
```

Отправить сообщение
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-producer --topic topic1 --bootstrap-server kafka1:9191
```
Каждая строка - одно сообщение. Прервать - Ctrl+Z

Получить сообщения
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-consumer --from-beginning --topic topic1 --bootstrap-server kafka1:9191 
```

Получить сообщения как consumer1
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-consumer --group consumer1 --topic topic1 --bootstrap-server kafka1:9191 
```

Отправить сообщение c ключом через двоеточие (key:value)
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-producer --topic topic1 --property "parse.key=true" --property "key.separator=:" --bootstrap-server kafka1:9191
```


# Репликация

```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-topics --create --topic test2 --partitions 1 --replication-factor 2 --bootstrap-server kafka1:9191
```

Отправить сообщение
```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-console-producer --topic test2 --bootstrap-server kafka1:9191
```
Каждая строка - одно сообщение. Прервать - Ctrl+Z


Давайте остановим лидера. А теперь кто лидер?
Отправим пару сообщений в топик.
Снова остановим лидера. Запустим "нелидера" - что с топиком?
Запустим бывшего лидера.

```shell
docker exec -ti kafka-otuskafka /usr/bin/kafka-topics --create --topic test3 --partitions 1 --replication-factor 2 --config min.insync.replicas=2 --bootstrap-server kafka1:9191
```

# Хранение сообщений

```shell
docker exec -ti kafka-otuskafka sh
```

cat /etc/kafka/server.properties
ls /var/lib/kafka
/usr/bin/kafka-dump-log --files 00000000000000000000.log
/usr/bin/kafka-dump-log --files 00000000000000000000.log --print-data-log