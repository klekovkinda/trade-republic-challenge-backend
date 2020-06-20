package io.klekovkinda.quotes.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InstrumentPriceAggregation {

    private final Map<Long, MinuteQuote> quotes;
    private final String instrumentId;
    private boolean available;
    private Long addTimeStamp;
    private Long deleteTimeStamp;

    public InstrumentPriceAggregation(String instrumentId) {
        quotes = new ConcurrentHashMap<Long, MinuteQuote>();
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
        quotes.putIfAbsent(epochMinutes, new MinuteQuote());
        mergeQuote(quotes.get(epochMinutes), quote);
    }

    private void mergeQuote(MinuteQuote minuteQuote, Quote quote) {
        if (minuteQuote.getOpenPrice() == null) {
            minuteQuote.setOpenPrice(quote.getPrice());
            minuteQuote.setHighPrice(quote.getPrice());
            minuteQuote.setLowPrice(quote.getPrice());
            minuteQuote.setClosePrice(quote.getPrice());
        } else {
            minuteQuote.setClosePrice(quote.getPrice());
            minuteQuote.setLowPrice(minuteQuote.getLowPrice().compareTo(quote.getPrice()) < 0 ? minuteQuote.getLowPrice() : quote.getPrice());
            minuteQuote.setHighPrice(minuteQuote.getLowPrice().compareTo(quote.getPrice()) > 0 ? minuteQuote.getLowPrice() : quote.getPrice());
        }
    }

    public boolean isAvailableNow() {
        return available;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public BigDecimal getLastQuote() {
        if (quotes.isEmpty()) {
            return null;
        }
        Long lastKey = Collections.max(quotes.keySet());
        MinuteQuote minuteQuote = quotes.get(lastKey);
        return minuteQuote.getClosePrice();
    }

    public synchronized List<CandleStick> getCandleSticks() {
        if (quotes.isEmpty()) {
            return null;
        }
        long nowMinutes = System.currentTimeMillis() / (60 * 1000);
        long end = deleteTimeStamp == null ? nowMinutes : deleteTimeStamp;
        long start = nowMinutes - 30;
        start = Math.max(start, addTimeStamp / (60 * 1000));
        MinuteQuote quote = null;
        long k = start;
        while (quote == null) {
            quote = quotes.get(k--);
        }
        List<CandleStick> candleSticks = new ArrayList<>();
        for (long i = start; i <= end; i++) {
            MinuteQuote minuteQuote = null;
            if (quotes.get(i) == null) {
                minuteQuote = quote;
            } else {
                minuteQuote = quotes.get(i);
                quote = minuteQuote;
            }
            candleSticks.add(new CandleStick(i, minuteQuote));
        }
        return candleSticks;
    }
}
