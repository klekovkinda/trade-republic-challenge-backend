package io.klekovkinda.quotes.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.klekovkinda.quotes.processor.MessageProcessor;
import io.klekovkinda.quotes.model.Message;
import io.klekovkinda.quotes.model.Payload;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

@ClientEndpoint
public class AppEndpoint<T extends Payload> {
    private static final Logger LOGGER = Logger.getLogger(AppEndpoint.class.getName());
    private final CountDownLatch latch;
    private final ObjectMapper mapper;
    private final TypeReference<Message<T>> typeReference;
    private final MessageProcessor messageProcessor;

    public AppEndpoint(TypeReference<Message<T>> typeReference,
                       MessageProcessor messageProcessor) {
        latch = new CountDownLatch(1);
        mapper = new ObjectMapper();
        this.typeReference = typeReference;
        this.messageProcessor = messageProcessor;
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info(String.format("Open web socket session[%s] for reading %s", session.getId(), typeReference.getType().toString()));
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            messageProcessor.processMessage(mapper.readValue(message, typeReference));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info(String.format("Session[%s] for reading %s has been closed because of %s", session.getId(), typeReference.getType().toString(), closeReason));
        this.latch.countDown();
    }

    public void inUse() throws InterruptedException {
        this.latch.await();
    }
}
