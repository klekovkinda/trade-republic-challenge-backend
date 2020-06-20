package io.klekovkinda.quotes.model;

import java.math.BigDecimal;

public class InstrumentPrice {
    private final String instrument;
    private final BigDecimal price;

    public InstrumentPrice(String instrument, BigDecimal price) {
        this.instrument = instrument;
        this.price = price;
    }

    public String getInstrument() {
        return instrument;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
