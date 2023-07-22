package ru.otus;

import java.util.ArrayList;
import java.util.List;

public class MessageReceiverGroup implements AutoCloseable {
    private final List<MessageReceiver> receivers = new ArrayList<>();
    public MessageReceiverGroup(String consumerGroup, int count, List<String> topics) {
        for (int i = 0; i < count; ++i) {
            receivers.add(new MessageReceiver(consumerGroup, i, topics));
        }
    }

    public MessageReceiverGroup(String consumerGroup, int count, String topic) {
        this(consumerGroup, count, List.of(topic));
    }

    @Override
    public void close() throws Exception {
        for (var r: receivers) {
            r.close();
        }
    }
}
