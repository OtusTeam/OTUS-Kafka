package ru.otus.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProducer implements AutoCloseable {
    protected final Thread thread = new Thread(this::send);
    protected final Map<String, Object> config;

    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public AbstractProducer(String name, Map<String, Object> config) {
        this.config = new HashMap<>(config);

        thread.setName("Producer." + name);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void close() throws Exception {
        thread.interrupt();
        thread.join();
    }

    public void join() throws Exception {
        thread.join();
    }

    protected abstract void doSend(KafkaProducer<String, String> producer) throws Exception;

    protected void afterSend() {
    }

    private void send() {
        try (var producer = new KafkaProducer<String, String>(config)) {
            doSend(producer);
        } catch (InterruptedException ignored) {
        } catch (Exception exception) {
            Utils.log.error("Exception!!!", exception);
        }
        Utils.log.info("Sender completed");
    }

    protected static <T> String convertToJson(T item) {
        return gson.toJson(item);
    }
}
