package io.klekovkinda.quotes.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import io.klekovkinda.quotes.configuration.Settings;
import io.klekovkinda.quotes.repository.InstrumentPriceRepository;
import org.glassfish.grizzly.http.util.HttpStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class RestService {

    private final InstrumentPriceRepository repository;
    private final ObjectMapper mapper;
    private HttpServer server;

    public RestService(InstrumentPriceRepository repository) {
        this.repository = repository;
        mapper = new ObjectMapper();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(Settings.REST_SERVER_PORT), 0);
        server.createContext("/instrument-price", exchange -> {
            try {
                final Headers headers = exchange.getResponseHeaders();
                final String requestMethod = exchange.getRequestMethod().toUpperCase();
                switch (requestMethod) {
                    case "GET":
                        // do something with the request parameters
                        headers.set("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));
                        String responseBody = mapper.writeValueAsString(repository.getInstrumentPrice());
                        final byte[] rawResponseBody = responseBody.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(HttpStatus.OK_200.getStatusCode(), rawResponseBody.length);
                        exchange.getResponseBody().write(rawResponseBody);
                        break;
                    default:
                        headers.set("Allow", "GET");
                        exchange.sendResponseHeaders(HttpStatus.METHOD_NOT_ALLOWED_405.getStatusCode(), -1);
                        break;
                }
            } finally {
                exchange.close();
            }
        });
        server.setExecutor(null);
        server.start();
    }
}
