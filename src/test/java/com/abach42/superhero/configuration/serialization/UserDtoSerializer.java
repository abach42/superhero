package com.abach42.superhero.configuration.serialization;

import java.io.IOException;

import org.springframework.context.annotation.Profile;

import com.abach42.superhero.dto.SuperheroUserDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

@Profile("test")
public class UserDtoSerializer extends JsonSerializer<SuperheroUserDto> {

    @Override
    public void serialize(SuperheroUserDto value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("email", value.email());
        gen.writeStringField("password", value.password());
        gen.writeStringField("role", value.role());
        gen.writeEndObject();
    }
}