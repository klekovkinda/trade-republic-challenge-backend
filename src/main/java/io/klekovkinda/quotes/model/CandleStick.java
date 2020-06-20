package io.klekovkinda.quotes.model;

import java.math.BigDecimal;

public class CandleStick {
    private final Long openTimestamp;
    private final BigDecimal openPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal closePrice;
    private final Long closeTimestamp;

    public CandleStick(Long key, MinuteQuote minuteQuote) {
        openTimestamp = key * 60 * 1000;
        closeTimestamp = (key + 1) * 60 * 1000;
        openPrice = minuteQuote.getOpenPrice();
        highPrice = minuteQuote.getHighPrice();
        lowPrice = minuteQuote.getLowPrice();
        closePrice = minuteQuote.getClosePrice();
    }

    public Long getOpenTimestamp() {
        return openTimestamp;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public Long getCloseTimestamp() {
        return closeTimestamp;
    }
}
