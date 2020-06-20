package io.klekovkinda.quotes.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class InstrumentPriceAggregation {

    private final ConcurrentMap<Long, List<Quote>> quotes;
    private final String instrumentId;
    private boolean available;
    private Long addTimeStamp;
    private Long deleteTimeStamp;

    public InstrumentPriceAggregation(String instrumentId) {
        quotes = new ConcurrentHashMap<Long, List<Quote>>();
        this.instrumentId = instrumentId;
    }

    public void deactivateInstrumentAt(Long deleteTimeStamp) {
        this.deleteTimeStamp = deleteTimeStamp;
        available = false;
    }

    public void activateInstrumentAt(Long addTimeStamp) {
        this.addTimeStamp = addTimeStamp;
        available = true;
    }

    public void addQuote(Quote quote) {
        Long epochMinutes = quote.getTimestamp() / (60 * 1000);
        quotes.putIfAbsent(epochMinutes, new ArrayList<>());
        quotes.get(epochMinutes).add(quote);
    }

    public boolean isAvailableNow() {
        return available;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public synchronized BigDecimal getLastQuote() {
        if (quotes.isEmpty()) {
            return null;
        }
        Long lastKey = Collections.max(quotes.keySet());
        List<Quote> quotesForMinute = quotes.get(lastKey);
        Collections.sort(quotesForMinute);
        return quotesForMinute.get(quotesForMinute.size() - 1).getPrice();
    }
}
