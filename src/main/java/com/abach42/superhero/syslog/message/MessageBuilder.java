package com.abach42.superhero.syslog.message;

public abstract class MessageBuilder<T extends Message, B extends MessageBuilder<T, B>> {
    protected final T message;

    protected MessageBuilder(T message) {
        this.message = message;
    }

    public B requestMethod(String requestMethod) {
        message.requestMethod = requestMethod;
        return self();
    }

    public B requestUrl(String requestUrl) {
        message.requestUrl = requestUrl;
        return self();
    }

    public B parametersCount(Integer parametersCount) {
        message.parametersCount = parametersCount;
        return self();
    }

    public B parameters(String parameters) {
        message.parameters = parameters;
        return self();
    }

    public B message(String messageContent) {
        message.message = messageContent;
        return self();
    }

    protected abstract B self();

    public T build() {
        return message;
    }
}