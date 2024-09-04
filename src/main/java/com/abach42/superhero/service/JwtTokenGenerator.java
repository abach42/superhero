package com.abach42.superhero.service;

public class JwtTokenGenerator extends AbstractTokenGenerator {
    public static final String SCOPE = "auth";
    public static final int EXP = 15;

    @Override
    int getExpirationMinutes() {
        return EXP;
    }

    @Override
    String getScope() {
        return SCOPE;
    }
}