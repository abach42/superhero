package com.abach42.superhero.configuration;

import java.time.LocalDate;
import com.abach42.superhero.entity.Superhero;

/*
 * static to be able to run in unit on missin IoC 
 */
public class TestDataConfiguration {
    public static final Superhero DUMMY_SUPERHERO = new Superhero("foo", "bar", LocalDate.of(1917, 1, 1), "Male", "foo", "foo");
}
