Запуск контейнера
```shell
cd kafka
docker compose start
```

Создать топик
```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-topics --create --topic test1 --partitions 1 --replication-factor 1 --bootstrap-server kafka1:9191
```

Получить список топиков
```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-topics --list --bootstrap-server kafka1:9191
```

Получить описание топика
```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-topics --describe --topic test1 --bootstrap-server kafka1:9191
```

# Репликация

```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-topics --create --topic test2 --partitions 1 --replication-factor 2 --bootstrap-server kafka1:9191
```

Отправить сообщение
```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-console-producer --topic test2 --bootstrap-server kafka1:9191
```
Каждая строка - одно сообщение. Прервать - Ctrl+Z


Давайте остановим лидера. А теперь кто лидер? 
Отправим пару сообщений в топик.
Снова остановим лидера. Запустим "нелидера" - что с топиком?
Запустим бывшего лидера.

```shell
docker exec -ti kafka1-otuskafka /usr/bin/kafka-topics --create --topic test3 --partitions 1 --replication-factor 2 --config min.insync.replicas=2 --bootstrap-server kafka1:9191
```

# Хранение сообщений

```shell
docker exec -ti kafka1-otuskafka sh
```

cat /etc/kafka/server.properties
ls /var/lib/kafka
/usr/bin/kafka-dump-log --files 00000000000000000000.log
/usr/bin/kafka-dump-log --files 00000000000000000000.log --print-data-log


# Удаление сообщений

docker exec -ti kafka1-otuskafka /usr/bin/kafka-configs  --entity-type brokers --entity-default --alter --add-config log.retention.ms=1000 --bootstrap-server kafka1:9191

docker exec -ti kafka1-otuskafka  /usr/bin/kafka-configs --bootstrap-server kafka1:9191 --entity-type topics --entity-name test --alter --add-config retention.ms=10000

for i in $(seq 1 2000); do echo "${i}"; sleep 1; done | /usr/bin/kafka-console-producer --bootstrap-server kafka1:9191 --topic test --sync