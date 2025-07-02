package com.abach42.superhero.configuration.serialization;

import com.abach42.superhero.dto.SuperheroDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Profile;

@Profile("test")
public class SuperheroDtoSerializer extends JsonSerializer<SuperheroDto> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void serialize(SuperheroDto value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.id());
        gen.writeStringField("alias", value.alias());
        gen.writeStringField("realName", value.realName());

        String dateOfBirthString = "";
        if (value.dateOfBirth() != null) {
            dateOfBirthString = value.dateOfBirth().format(formatter);
        }
        gen.writeStringField("dateOfBirth", dateOfBirthString);
        gen.writeStringField("gender", value.gender());
        gen.writeStringField("occupation", value.occupation());
        gen.writeStringField("originStory", value.originStory());
        gen.writeObjectField("user", value.user());
        gen.writeEndObject();
    }
}
