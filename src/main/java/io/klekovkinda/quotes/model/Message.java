package io.klekovkinda.quotes.model;

public class Message<T extends Payload> {
    private MessageType type;

    private T data;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
