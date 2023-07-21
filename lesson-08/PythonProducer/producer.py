from kafka import KafkaProducer


def produce(topic, servers):
    producer = KafkaProducer(bootstrap_servers=servers, value_serializer=str.encode)

    for i in range(1000):
        producer.send(topic, key=i.to_bytes(length=2, byteorder='big'), value='Message '+str(i))

    producer.flush()


if __name__ == "__main__":
    produce('test', 'localhost:9092')
