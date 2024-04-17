package com.abach42.superhero.config;

/*
 * Concatenation of base path for api, to keep other resources in place and 
 * to avoid complicated header version constraints or custom request handler implementations. 
 * Waiting for a simple solution in spring framework some day...
 */
public class PathConfig {
    protected final static String ALIAS = "/api";
    protected final static String VERSION = "/v1";
    public final static String BASE_URI = ALIAS + VERSION;
}