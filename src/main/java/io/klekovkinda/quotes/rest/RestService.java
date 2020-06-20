package io.klekovkinda.quotes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import io.klekovkinda.quotes.configuration.Settings;
import io.klekovkinda.quotes.repository.Repository;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestService {

    private final Repository repository;
    private final ObjectMapper mapper;
    private HttpServer server;

    public RestService(Repository repository) {
        this.repository = repository;
        mapper = new ObjectMapper();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Settings.REST_SERVER_PORT), 0);
        server.createContext("/instrument-price", new InstrumentPriceHandler(mapper, repository));
        server.createContext("/candlesticks", new CandlesticksHandler(mapper, repository));
        server.setExecutor(null);
        server.start();
    }
}
