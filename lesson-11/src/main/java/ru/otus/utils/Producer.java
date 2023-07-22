package ru.otus.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements AutoCloseable {
    private final Thread thread = new Thread(this::send);
    private final String topic;
    private final Map<String, Object> config;
    private final AtomicInteger lastReceived;
    private final AtomicInteger lastSend = new AtomicInteger();
    private final int startKey;

    public Producer(String topic, Map<String, Object> config, AtomicInteger lastReceived) {
        this(topic, config, lastReceived, 0);
    }

    public Producer(String topic, Map<String, Object> config, AtomicInteger lastReceived, int startKey) {
        this.topic = topic;
        this.config = config;
        this.lastReceived = lastReceived;
        this.startKey = startKey;

        thread.setName("Sender." + topic);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void close() throws Exception {
        thread.interrupt();
        thread.join();
    }

    public int getLastSend() {
        return lastSend.get();
    }

    private void send() {
        int i = startKey;
        try (var producer = new KafkaProducer<String, String>(config)) {
            while (!Thread.interrupted()) {
                var savedI = i;
                var key = Integer.toString(i);
                var record = new ProducerRecord<>(topic, key, "some data");
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        Utils.log.info("Sender to {} CANT send {}", topic, key);
                    } else {
                        lastSend.set(savedI);
                    }
                });
                //Thread.sleep(1);
                if (i % 100000 == 0) {
                    //Utils.log.info("Sender to {} sent {}", topic, i);
                    //Thread.sleep(100);
                }
                if (lastReceived != null) {
                    int counter = 0;
                    while (i - lastReceived.get() > 1_000_000) {
                        producer.flush();
                        Thread.sleep(100);
                        counter += 1;
                    }
                    if (counter > 0) {
                        //Utils.log.info("Sender to {} sent {}, SLEEP", topic, i);
                    }

                }

                i += 1;
            }
        } catch (Exception ignored) {
        }
        Utils.log.info("Sender to {} completed, no={}", topic, i);
    }
}
