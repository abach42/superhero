package com.abach42.superhero.syslog.message;

public class ActionMessageBuilder extends MessageBuilder<ActionMessage, ActionMessageBuilder> {
    public ActionMessageBuilder() {
        super(new ActionMessage());
    }

    public ActionMessageBuilder controller(String controller) {
        message.controller = controller;
        return this;
    }

    public ActionMessageBuilder action(String action) {
        message.action = action;
        return this;
    }

    @Override
    protected ActionMessageBuilder self() {
        return this;
    }
}