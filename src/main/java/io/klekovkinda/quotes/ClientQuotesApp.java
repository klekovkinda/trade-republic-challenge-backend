package io.klekovkinda.quotes;

import com.fasterxml.jackson.core.type.TypeReference;
import io.klekovkinda.quotes.model.Instrument;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Quote;
import io.klekovkinda.quotes.processor.MessageProcessor;
import io.klekovkinda.quotes.repository.InstrumentPriceRepository;
import io.klekovkinda.quotes.repository.Repository;
import io.klekovkinda.quotes.rest.RestService;
import io.klekovkinda.quotes.ws.AppEndpoint;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientQuotesApp {

    public static void main(String[] args) {
        ClientManager client = ClientManager.createClient();
        TypeReference<Message<Quote>> quoteTypeReference = new TypeReference<Message<Quote>>() {
        };
        TypeReference<Message<Instrument>> instrumentTypeReference = new TypeReference<Message<Instrument>>() {
        };
        Repository repository = new InstrumentPriceRepository();
        MessageProcessor messageProcessor = new MessageProcessor(repository);
        RestService restService = new RestService(repository);
        AppEndpoint<Quote> quotesEndpoint = new AppEndpoint<Quote>(quoteTypeReference, messageProcessor);
        AppEndpoint<Instrument> instrumentEndpoint = new AppEndpoint<Instrument>(instrumentTypeReference, messageProcessor);
        try {
            client.connectToServer(instrumentEndpoint, new URI("ws://localhost:8080/instruments"));
            client.connectToServer(quotesEndpoint, new URI("ws://localhost:8080/quotes"));
            restService.start();
            instrumentEndpoint.inUse();
            quotesEndpoint.inUse();
        } catch (DeploymentException | URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
