package com.abach42.superhero.shared.api;

/*
 * Concatenation of base path for api, to keep other resources in place and
 * to avoid complicated header version constraints or custom request handler implementations.
 * Waiting for a simple solution in spring framework some day...
 */
public final class PathConfig {

    private static final String ALIAS = "/api";
    private static final String VERSION = "/v1";
    public static final String BASE_URI = ALIAS + VERSION;

    public static final String AUTH = BASE_URI + "/auth";
    public static final String SUPERHEROES = BASE_URI + "/superheroes";
    public static final String SKILL_PROFILES = SUPERHEROES + "/{superheroId}" + "/skill-profiles";
    public static final String SKILLS = BASE_URI + "/skills";
}