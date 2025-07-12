package com.abach42.superhero.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.abach42.superhero.superhero.SuperheroDto;
import com.abach42.superhero.testconfiguration.TestStubs;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("user")})
public class ApplicationUserNoPasswordTest {

    @Test
    @DisplayName("Should serialize SuperheroDto correctly - no password")
    void shouldSerializeSuperheroDto() throws Exception {
        SuperheroDto dto = TestStubs.getSuperheroDtoStubWithPassword();

        String json = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(dto);

        assertThat(json).doesNotContain("password");
        assertThat(json).doesNotContain("bar");
    }
}