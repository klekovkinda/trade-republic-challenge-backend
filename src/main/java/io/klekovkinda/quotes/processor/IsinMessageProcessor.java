package io.klekovkinda.quotes.processor;

import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.repository.Repository;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class IsinMessageProcessor implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(IsinMessageProcessor.class.getName());

    private final ConcurrentLinkedDeque<Message<? extends Payload>> messageCollection;
    private final String aggregatorId;
    private final Repository repository;
    private boolean running;

    public IsinMessageProcessor(String aggregatorId, Repository repository) {
        messageCollection = new ConcurrentLinkedDeque<Message<? extends Payload>>();
        this.aggregatorId = aggregatorId;
        this.repository = repository;
    }

    public boolean isRunning() {
        return running;
    }

    public void addMessage(Message<? extends Payload> message) {
        messageCollection.add(message);
    }

    @Override
    public void run() {
        running = true;
        while (!messageCollection.isEmpty()) {
            Message<? extends Payload> message = messageCollection.pop();
            repository.save(message);
            LOGGER.info(String.format("%s IsinMessageProcessor processed message from %d", aggregatorId, message.getData().getTimestamp()));
        }
        running = false;
    }
}
