package io.klekovkinda.quotes.model;

import java.math.BigDecimal;

public class Quote extends Payload implements Comparable<Quote> {
    private BigDecimal price;
    private String isin;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    @Override
    public int compareTo(Quote o) {
        return Long.compare(this.getTimestamp(), o.getTimestamp());
    }
}
