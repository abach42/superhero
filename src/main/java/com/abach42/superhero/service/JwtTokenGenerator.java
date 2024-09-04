package com.abach42.superhero.service;

public class JwtTokenGenerator extends AbstractTokenGenerator {
    public static final String SCOPE = "auth";

    @Override
    int getExpirationMinutes() {
        return 15;
    }

    @Override
    String getScope() {
        return SCOPE;
    }
}