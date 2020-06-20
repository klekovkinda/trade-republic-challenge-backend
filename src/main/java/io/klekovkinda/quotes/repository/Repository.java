package io.klekovkinda.quotes.repository;

import io.klekovkinda.quotes.model.CandleStick;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;

import java.util.List;

public interface Repository {
    void save(Message<? extends Payload> message);

    List<Object> getLatestInstrumentsPrice();

    List<CandleStick> getCandlesticks(String instrument);
}
