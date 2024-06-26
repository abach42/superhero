package com.abach42.superhero.config.api;

/*
 * Concatenation of base path for api, to keep other resources in place and 
 * to avoid complicated header version constraints or custom request handler implementations. 
 * Waiting for a simple solution in spring framework some day...
 */
public final class PathConfig {
    protected static final String ALIAS = "/api";
    protected static final String VERSION = "/v1";
    public static final String BASE_URI = ALIAS + VERSION;

    public static final String TOKENS = BASE_URI;
    public static final String SUPERHEROES = BASE_URI + "/superheroes";
    public static final String SKILLS = BASE_URI + "/skills";
    public static final String SKILLPROFILES = SUPERHEROES + "/{superheroId}" + "/skillprofiles";
}