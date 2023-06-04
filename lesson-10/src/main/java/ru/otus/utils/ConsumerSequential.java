package ru.otus.utils;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumerSequential extends AbstractConsumer {
    public final AtomicInteger lastReceive = new AtomicInteger();
    private int nextNo;

    public ConsumerSequential(String name, String topic, Map<String, Object> config) {
        super(name, topic, config);

        this.config.put(ConsumerConfig.GROUP_ID_CONFIG, name + "-" + UUID.randomUUID());
        this.config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        this.config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 2000);

        this.thread.start();
    }

    @Override
    protected void onFinishProcess() {
        Utils.log.info("Complete! {}", nextNo-1);
    }

    @Override
    protected void processOne(ConsumerRecord<String, String> record) {
        var current = Integer.parseInt(record.key());
        if (current != nextNo) {
            Utils.log.warn("Expect {}, received {}, delta {}", nextNo, current, nextNo - current);
        }
        lastReceive.set(current);
        nextNo = current + 1;
    }
}
