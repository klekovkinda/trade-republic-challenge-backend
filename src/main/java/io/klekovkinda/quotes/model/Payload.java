package io.klekovkinda.quotes.model;

public abstract class Payload {
    private final long timestamp;

    protected Payload() {
        timestamp = System.currentTimeMillis();
    }

    public abstract String getIsin();

    public final long getTimestamp() {
        return timestamp;
    }

}
