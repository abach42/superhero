package com.abach42.superhero.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {
    public ApiException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}