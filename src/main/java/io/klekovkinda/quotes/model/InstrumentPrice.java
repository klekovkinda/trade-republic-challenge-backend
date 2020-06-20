package io.klekovkinda.quotes.model;

import java.math.BigDecimal;

public class InstrumentPrice {
    private final String isin;
    private final BigDecimal lastPrice;
    private final Long lastPriceUpdate;
    private final long lastAddDeleteUpdate;
    private final boolean available;

    public InstrumentPrice(String isin, BigDecimal lastPrice, Long lastPriceUpdate, long lastAddDeleteUpdate, boolean available) {
        this.isin = isin;
        this.lastPrice = lastPrice;
        this.lastPriceUpdate = lastPriceUpdate;
        this.lastAddDeleteUpdate = lastAddDeleteUpdate;
        this.available = available;
    }

    public String getIsin() {
        return isin;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public Long getLastPriceUpdate() {
        return lastPriceUpdate;
    }

    public long getLastAddDeleteUpdate() {
        return lastAddDeleteUpdate;
    }

    public boolean isAvailable() {
        return available;
    }


    public static class InstrumentPriceDTO {
        private String isin;

        private BigDecimal latestPrice;

        public InstrumentPriceDTO() {
        }

        public InstrumentPriceDTO(String isin, BigDecimal latestPrice) {
            this.isin = isin;
            this.latestPrice = latestPrice;
        }

        public String getIsin() {
            return isin;
        }

        public void setIsin(String isin) {
            this.isin = isin;
        }

        public BigDecimal getLatestPrice() {
            return latestPrice;
        }

        public void setLatestPrice(BigDecimal latestPrice) {
            this.latestPrice = latestPrice;
        }
    }
}
