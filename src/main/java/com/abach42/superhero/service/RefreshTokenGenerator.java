package com.abach42.superhero.service;

public class RefreshTokenGenerator extends AbstractTokenGenerator {

    public static final String SCOPE = "refresh";

    @Override
    int getExpirationMinutes() {
        return 120;
    }

    @Override
    String getScope() {
        return SCOPE;
    }
}