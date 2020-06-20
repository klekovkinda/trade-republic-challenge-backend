package io.klekovkinda.quotes.repository;

import io.klekovkinda.quotes.model.InstrumentPrice;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;
import io.klekovkinda.quotes.model.Quote;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class InstrumentPriceRepository {

    private final ConcurrentMap<String, InstrumentPrice> instrumentPrice;

    public InstrumentPriceRepository() {
        this.instrumentPrice = new ConcurrentHashMap<String, InstrumentPrice>() {
        };
    }

    public synchronized void save(Message<? extends Payload> message) {
        String isin = message.getData().getIsin();
        Long isinUpdate = message.getTimestamp();
        InstrumentPrice lastInstrumentSnapshot = instrumentPrice.get(isin);
        InstrumentPrice updatedInstrument = null;
        switch (message.getType()) {
            case ADD: {
                if (lastInstrumentSnapshot != null) {
                    if (lastInstrumentSnapshot.getLastAddDeleteUpdate() <= isinUpdate) {
                        updatedInstrument = new InstrumentPrice(isin, lastInstrumentSnapshot.getLastPrice(),
                                lastInstrumentSnapshot.getLastPriceUpdate(),
                                isinUpdate,
                                true);
                    }
                } else {
                    updatedInstrument = new InstrumentPrice(isin, null, message.getTimestamp(), message.getTimestamp(), true);
                }
                instrumentPrice.put(isin, updatedInstrument);
                break;
            }
            case QUOTE: {
                Quote quote = (Quote) message.getData();
                if (lastInstrumentSnapshot != null) {
                    if (lastInstrumentSnapshot.getLastPriceUpdate() <= isinUpdate) {
                        updatedInstrument = new InstrumentPrice(isin, quote.getPrice(),
                                isinUpdate,
                                lastInstrumentSnapshot.getLastAddDeleteUpdate(),
                                lastInstrumentSnapshot.isAvailable());
                    }
                } else {
                    updatedInstrument = new InstrumentPrice(isin, quote.getPrice(), message.getTimestamp(), message.getTimestamp(), true);
                }
                instrumentPrice.put(isin, updatedInstrument);
                break;
            }
            case DELETE: {
                if (lastInstrumentSnapshot != null) {
                    if (lastInstrumentSnapshot.getLastAddDeleteUpdate() <= isinUpdate) {
                        updatedInstrument = new InstrumentPrice(isin, lastInstrumentSnapshot.getLastPrice(),
                                lastInstrumentSnapshot.getLastPriceUpdate(),
                                isinUpdate,
                                false);
                    }
                } else {
                    updatedInstrument = new InstrumentPrice(isin, null, message.getTimestamp(), message.getTimestamp(), false);
                }
                instrumentPrice.put(isin, updatedInstrument);
                break;
            }
        }
    }

    public synchronized List<InstrumentPrice.InstrumentPriceDTO> getInstrumentPrice() {
        return instrumentPrice.values().stream().filter(InstrumentPrice::isAvailable).map(value -> new InstrumentPrice.InstrumentPriceDTO(value.getIsin(), value.getLastPrice())).collect(Collectors.toList());
    }
}
