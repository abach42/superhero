package com.abach42.superhero.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus
public class SuperheroException extends ResponseStatusException {
    public SuperheroException(HttpStatusCode status, String reason) {
        super(status, reason);
    }
}