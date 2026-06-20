from kafka import KafkaConsumer


def consume(topics, servers, group_id):
    consumer = KafkaConsumer(bootstrap_servers=servers, group_id=group_id, value_deserializer=bytes.decode,
                             auto_offset_reset='earliest', consumer_timeout_ms=10000)
    consumer.subscribe(topics)

    for message in consumer:
        print("%d:%d: key=%s value=%s" % (message.partition, message.offset,
                                          int.from_bytes(message.key, "big"), message.value))


if __name__ == "__main__":
    consume(['test'], 'localhost:9092', 'g1')
