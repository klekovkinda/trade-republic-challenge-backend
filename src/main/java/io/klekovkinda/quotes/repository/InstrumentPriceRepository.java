package io.klekovkinda.quotes.repository;

import io.klekovkinda.quotes.model.InstrumentPriceAggregation;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.model.Quote;

import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InstrumentPriceRepository implements Repository {

    private final ConcurrentMap<String, InstrumentPriceAggregation> instrumentsPrice;

    public InstrumentPriceRepository() {
        this.instrumentsPrice = new ConcurrentHashMap<String, InstrumentPriceAggregation>();
    }

    public void save(Message<? extends Payload> message) {
        instrumentsPrice.putIfAbsent(message.getData().getIsin(), new InstrumentPriceAggregation(message.getData().getIsin()));
        InstrumentPriceAggregation instrumentPriceAggregation = instrumentsPrice.get(message.getData().getIsin());
        switch (message.getType()) {
            case ADD: {
                instrumentPriceAggregation.activateInstrumentAt(message.getData().getTimestamp());
                break;
            }
            case DELETE: {
                instrumentPriceAggregation.deactivateInstrumentAt(message.getData().getTimestamp());
                break;
            }
            case QUOTE: {
                instrumentPriceAggregation.addQuote((Quote) message.getData());
            }
        }
    }

    public synchronized List<Object> getLatestInstrumentsPrice() {
        return instrumentsPrice.values().stream().filter(InstrumentPriceAggregation::isAvailableNow).map(value -> new AbstractMap.SimpleEntry(value.getInstrumentId(), value.getLastQuote())).collect(Collectors.toList());
    }
}
