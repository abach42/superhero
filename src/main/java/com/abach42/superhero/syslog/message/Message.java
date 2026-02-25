package com.abach42.superhero.syslog.message;

import java.util.Arrays;
import java.util.List;

public abstract class Message {
    protected static final String DELIMITER = "|";
    
    public String requestMethod;
    public String requestUrl;
    public Integer parametersCount;
    public String parameters;
    
    public String message;

    @Override
    public String toString() {
        return "requestMethod: " + requestMethod
                + DELIMITER + "requestUrl: " + requestUrl
                + DELIMITER + "parametersCount: " + parametersCount
                + DELIMITER + "parameters: " + parameters
                + DELIMITER + "message: " + message;
    }
    
    public static List<String> convertToListOfStrings(String message) {
        if (message == null) {
            return List.of();
        }
        return Arrays.asList(message.split("\\" + DELIMITER));
    }
}
