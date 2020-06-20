package io.klekovkinda.quotes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.klekovkinda.quotes.repository.Repository;
import org.glassfish.grizzly.http.util.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InstrumentPriceHandler implements HttpHandler {
    private final ObjectMapper mapper;
    private final Repository repository;

    public InstrumentPriceHandler(ObjectMapper mapper, Repository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String responseBody = mapper.writeValueAsString(repository.getLatestInstrumentsPrice());
            final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpStatus.OK_200.getStatusCode(), rawResponseBody.length);
            httpExchange.getResponseBody().write(rawResponseBody);
        } finally {
            httpExchange.close();
        }
    }
}
