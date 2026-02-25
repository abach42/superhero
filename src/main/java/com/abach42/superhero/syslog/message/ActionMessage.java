package com.abach42.superhero.syslog.message;

public class ActionMessage extends Message {
    public String controller;
    public String action;

    @Override
    public String toString() {
        return super.toString()
                + DELIMITER + "controller: " + controller
                + DELIMITER + "action: " + action;
    }
}
