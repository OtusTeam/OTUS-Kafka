package ru.otus.utils;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractConsumer implements AutoCloseable {
    protected final Thread thread = new Thread(this::process);
    private final String topic;
    protected final Map<String, Object> config;
    protected final String name;

    public AbstractConsumer(String name, String topic, Map<String, Object> config) {
        this.topic = topic;
        this.name = name;

        this.config = new HashMap<>(config);

        thread.setName("MessagesReceiver." + name);
        thread.setDaemon(true);
    }

    protected void onStartProcess() {
        Utils.log.info("Start!");
    }

    protected void onFinishProcess() {
        Utils.log.info("Complete!");
    }

    protected abstract void processOne(ConsumerRecord<String, String> record);

    private void process() {
        onStartProcess();

        try (var consumer = new KafkaConsumer<String, String>(config)) {
            consumer.subscribe(List.of(topic));
            Utils.log.info("Subscribed");


            while (!Thread.interrupted()) {
                var read = consumer.poll(Duration.ofSeconds(1));
                for (var record : read) {
                    processOne(record);
                }
            }
        }
        catch (Exception ignored) {
        }

        onFinishProcess();
    }

    @Override
    public void close() throws Exception {
        thread.interrupt();
        thread.join();
    }
}
