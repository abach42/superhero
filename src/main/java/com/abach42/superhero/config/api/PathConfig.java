package com.abach42.superhero.config.api;

/*
 * Concatenation of base path for api, to keep other resources in place and 
 * to avoid complicated header version constraints or custom request handler implementations. 
 * Waiting for a simple solution in spring framework some day...
 */
public final class PathConfig {
    protected final static String ALIAS = "/api";
    protected final static String VERSION = "/v1";
    public final static String BASE_URI = ALIAS + VERSION;

    public final static String TOKENS = BASE_URI;
    public final static String SUPERHEROES = BASE_URI + "/superheroes";
    public final static String SKILLS = BASE_URI + "/skills";
    public final static String SKILLPROFILES = SUPERHEROES + "/{superheroId}" + "/skillprofiles";
}