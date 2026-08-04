package ru.otus.lesson17new.common;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class DeduplicationStore {
    private final Set<String> processedEvents = ConcurrentHashMap.newKeySet();

    public boolean alreadyProcessed(String consumerName, String eventId) {
        return processedEvents.contains(key(consumerName, eventId));
    }

    public void markProcessed(String consumerName, String eventId) {
        processedEvents.add(key(consumerName, eventId));
    }

    private String key(String consumerName, String eventId) {
        return consumerName + ":" + eventId;
    }
}
