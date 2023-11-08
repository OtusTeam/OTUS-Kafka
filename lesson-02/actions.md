Запуск контейнеров
```shell
cd kafka
docker compose up -d
docker compose ps -a
```

Получить список топиков
```shell
docker exec -ti kafka1 /usr/bin/kafka-topics --list --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094
```

Создать топик topic1
```shell
docker exec -ti kafka1 /usr/bin/kafka-topics --create --topic topic1 --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094
```

Отправить сообщение
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-producer --topic topic1 --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094
```
Каждая строка - одно сообщение. Прервать - Ctrl+D

Получить сообщения
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-consumer --from-beginning --topic topic1 --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094 
```
Прервать - Ctrl+C

Отправить сообщение c ключом через двоеточие (key:value)
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-producer --topic topic1 --property parse.key=true --property key.separator=: --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094
```

Получить сообщения
```shell
docker exec -ti kafka1 /usr/bin/kafka-console-consumer --topic topic1 --property print.key=true --property print.offset=true --from-beginning --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094
```
Прервать - Ctrl+C

Останавливаем контейнеры
```shell
docker compose stop
docker container prune -f
docker volume prune -f
```
