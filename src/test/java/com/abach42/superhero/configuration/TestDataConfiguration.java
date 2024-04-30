package com.abach42.superhero.configuration;

import java.time.LocalDate;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.SuperheroUser;
import com.abach42.superhero.entity.dto.SuperheroDto;
import com.abach42.superhero.entity.dto.UserDto;

/*
 * static to be able to run in unit on missin IoC 
 */
public class TestDataConfiguration {
    public static final Superhero getSuperheroStub() {
        return new Superhero("foo", "bar", LocalDate.of(1970, 1, 1), "Male", "foo", 
            "foo", new SuperheroUser("foo", null, "USER"));
    }

    public static final Superhero getSuperheroStubWithPassword() {
        return new Superhero("some", "bar", LocalDate.of(1970, 1, 1), "Male", "foo", 
            "foo", new SuperheroUser("unique", "foo", "USER"));
    }

    public static final SuperheroDto getSuperheroDtoStub() {
        return SuperheroDto.fromDomain(getSuperheroStub());
    }

    public static final SuperheroDto getSuperheroDtoStubEmpty() {
        return new SuperheroDto(null, null, null, null, null, null, null, null);
    }

    public static final SuperheroDto getSuperheoDtoStupWithPaword() {
        return new SuperheroDto(null, "new", "foo", LocalDate.of(1970,1,1), "foo",
                                    "foo", "foo", new UserDto("new", "bar", "USER"));
    }
}
