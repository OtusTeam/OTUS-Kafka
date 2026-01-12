# Consumer Partition Assignment Test

Проверка работы стратегии назначения разделов тем потребителям.

Создаём две темы по три раздела в каждой

    kafka-topics.sh --create --topic t1 --partitions 3 --replication-factor 3 --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092

    kafka-topics.sh --create --topic t2 --partitions 3 --replication-factor 3 --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092

## RangeAssignor

* Открываем первый терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar RangeAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод 

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t1, Partition: 1
Topic: t1, Partition: 2
Topic: t2, Partition: 0
Topic: t2, Partition: 1
Topic: t2, Partition: 2

After subscribe
Topic: t1, Partition: 2
Topic: t1, Partition: 1
Topic: t2, Partition: 2
Topic: t1, Partition: 0
Topic: t2, Partition: 1
Topic: t2, Partition: 0

* Открываем второй терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar RangeAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод во втором терминале

After subscribe

onPartitionsAssigned
Topic: t1, Partition: 2
Topic: t2, Partition: 2

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t1, Partition: 1
Topic: t2, Partition: 0
Topic: t2, Partition: 1

* Открываем третий терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar RangeAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод во третьем терминале

After subscribe

onPartitionsAssigned
Topic: t1, Partition: 2
Topic: t2, Partition: 2

Вывод во втором терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 1
Topic: t2, Partition: 1

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t2, Partition: 0

* В третьем терминале останавливаем потребителя
^C
Вывод во втором терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 2
Topic: t2, Partition: 2

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t1, Partition: 1
Topic: t2, Partition: 0
Topic: t2, Partition: 1

## StickyAssignor

* Открываем первый терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar StickyAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод 

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t1, Partition: 1
Topic: t1, Partition: 2
Topic: t2, Partition: 0
Topic: t2, Partition: 1
Topic: t2, Partition: 2

After subscribe
Topic: t1, Partition: 2
Topic: t1, Partition: 1
Topic: t2, Partition: 2
Topic: t1, Partition: 0
Topic: t2, Partition: 1
Topic: t2, Partition: 0

* Открываем второй терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar StickyAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод во втором терминале

After subscribe

onPartitionsAssigned
Topic: t1, Partition: 2
Topic: t2, Partition: 1
Topic: t2, Partition: 2

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t1, Partition: 1
Topic: t2, Partition: 0

* Открываем третий терминал и запускаем потребителя

    java -jar ConsumerPartitionAssignment.jar StickyAssignor kafka1:9092,kafka2:9092,kafka3:9092

Вывод во третьем терминале

After subscribe

onPartitionsAssigned
Topic: t1, Partition: 1
Topic: t2, Partition: 2

Вывод во втором терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 2
Topic: t2, Partition: 1

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t2, Partition: 0

* В третьем терминале останавливаем потребителя
^C

Вывод во втором терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 1
Topic: t1, Partition: 2
Topic: t2, Partition: 1

Вывод в первом терминале

onPartitionsRevoked

onPartitionsAssigned
Topic: t1, Partition: 0
Topic: t2, Partition: 0
Topic: t2, Partition: 2
