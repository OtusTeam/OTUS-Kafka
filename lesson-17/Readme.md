# Lesson 17 New: Event-Driven Architecture with Spring for Apache Kafka

Практика показывает событийно-ориентированную архитектуру на нескольких экземплярах одного Spring Boot приложения.
Каждый экземпляр запускается с отдельным Spring profile и играет роль отдельного сервиса.

## Что демонстрируем

- переход от синхронного запроса к асинхронной публикации события;
- choreography: `payments` и `inventory` независимо реагируют на `OrderCreated`;
- saga/process manager: `saga` собирает решения сервисов и выпускает финальное событие;
- idempotent consumer: повторное событие с тем же `eventId` не обрабатывается повторно;
- transactional producer: публикация производных событий идет через `KafkaTemplate.executeInTransaction`;
- DLT: техническая ошибка в обработчике уходит в `lesson17.order.created.dlt`;
- read model/projection: `audit` собирает историю событий по заказу;
- Kafka Streams: `streams` маршрутизирует дорогие и обычные заказы в разные топики.

## Запуск Kafka

```shell
cd lesson-17
docker compose up -d
```

Kafdrop: http://localhost:9000

В демо используется 2 Kafka broker и топики с 2 партициями. Так проще показать распределение партиций между брокерами и масштабирование consumer group без лишней асимметрии.

## Запуск сервисов

Откройте несколько терминалов из директории `lesson-17`.

Если код менялся после предыдущего запуска или видите `ClassNotFoundException` / `NoClassDefFoundError`, остановите старый `bootRun` через `Ctrl+C` и один раз пересоберите проект:

```bash
./gradlew clean classes
```

PowerShell / CMD:

```shell
.\gradlew.bat bootRun --args="--spring.profiles.active=orders"
.\gradlew.bat bootRun --args="--spring.profiles.active=payments"
.\gradlew.bat bootRun --args="--spring.profiles.active=inventory"
.\gradlew.bat bootRun --args="--spring.profiles.active=saga"
.\gradlew.bat bootRun --args="--spring.profiles.active=notifications"
.\gradlew.bat bootRun --args="--spring.profiles.active=audit"
.\gradlew.bat bootRun --args="--spring.profiles.active=streams"
```

Git Bash / Linux / macOS:

```bash
./gradlew bootRun --args="--spring.profiles.active=orders"
./gradlew bootRun --args="--spring.profiles.active=payments"
./gradlew bootRun --args="--spring.profiles.active=inventory"
./gradlew bootRun --args="--spring.profiles.active=saga"
./gradlew bootRun --args="--spring.profiles.active=notifications"
./gradlew bootRun --args="--spring.profiles.active=audit"
./gradlew bootRun --args="--spring.profiles.active=streams"
```

Порты:

- `orders`: `8010`
- `payments`: `8020`
- `inventory`: `8030`
- `saga`: `8040`
- `notifications`: `8050`
- `audit`: `8060`
- `streams`: `8070`
