package io.klekovkinda.quotes.processor;

import io.klekovkinda.quotes.configuration.Settings;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.repository.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class MessageProcessor {
    private static final Logger LOGGER = Logger.getLogger(MessageProcessor.class.getName());
    private final ExecutorService threadPoolExecutor;
    private final ConcurrentHashMap<String, IsinMessageProcessor> isinQuoteAggregatorMap;
    private final Repository instrumentPriceRepository;

    public MessageProcessor(Repository instrumentPriceRepository) {
        this.instrumentPriceRepository = instrumentPriceRepository;
        threadPoolExecutor = Executors.newFixedThreadPool(Settings.CONSUMERS_THREAD_POOL_SIZE);
        isinQuoteAggregatorMap = new ConcurrentHashMap<>();
    }

    public void processMessage(Message<? extends Payload> message) {
        String isin = message.getData().getIsin();
        isinQuoteAggregatorMap.putIfAbsent(isin, new IsinMessageProcessor(isin, instrumentPriceRepository));
        IsinMessageProcessor messageConsumer = isinQuoteAggregatorMap.get(message.getData().getIsin());
        messageConsumer.addMessage(message);
        if (!messageConsumer.isRunning()) {
            threadPoolExecutor.execute(messageConsumer);
        }
        LOGGER.fine(String.format("In total %d isinMessageProcessors has been created", isinQuoteAggregatorMap.size()));
    }
}
