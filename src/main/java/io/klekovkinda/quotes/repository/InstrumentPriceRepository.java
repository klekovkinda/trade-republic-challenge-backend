package io.klekovkinda.quotes.repository;

import io.klekovkinda.quotes.model.CandleStick;
import io.klekovkinda.quotes.model.InstrumentPrice;
import io.klekovkinda.quotes.model.InstrumentPriceAggregation;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.model.Quote;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InstrumentPriceRepository implements Repository {

    private final Map<String, InstrumentPriceAggregation> instrumentsPrice;

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

    public List<Object> getLatestInstrumentsPrice() {
        return instrumentsPrice.values().stream()
                .filter(InstrumentPriceAggregation::isAvailableNow)
                .map(value -> new InstrumentPrice(value.getInstrumentId(), value.getLastQuote()))
                .collect(Collectors.toList());
    }

    public List<CandleStick> getCandlesticks(String instrument) {
        return instrumentsPrice.get(instrument).getCandleSticks();
    }
}
