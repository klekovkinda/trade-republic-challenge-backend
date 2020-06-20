package io.klekovkinda.quotes.processor;

import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.repository.InstrumentPriceRepository;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Logger;

public class IsinQuoteAggregator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(IsinQuoteAggregator.class.getName());

    private final ConcurrentLinkedDeque<Message<? extends Payload>> messageCollection;
    private final String aggregatorId;
    private final InstrumentPriceRepository repository;
    private boolean running;

    public IsinQuoteAggregator(String aggregatorId, InstrumentPriceRepository repository) {
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
        LOGGER.info(String.format("%s isin consumed %d messages", aggregatorId, messageCollection.size()));
        while (!messageCollection.isEmpty()) {
            Message<? extends Payload> message = messageCollection.pop();
            LOGGER.info(String.format("%s - %d", aggregatorId, message.getTimestamp()));
            repository.save(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        running = false;
    }
}
