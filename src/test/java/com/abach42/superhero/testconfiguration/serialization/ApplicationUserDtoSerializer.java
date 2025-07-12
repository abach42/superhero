package com.abach42.superhero.testconfiguration.serialization;

import com.abach42.superhero.user.ApplicationUserDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Custom serializer for the {@link ApplicationUserDto} class, to overcome password hiding in tests
 * for input.
 * Be careful in usage, normally passwords have to be hidden.
 * <br/>
 * This class is utilized in the configuration class where the Jackson {@link ObjectMapper}
 * is set up with this custom serializer for handling {@link ApplicationUserDto} objects.
 */
@Profile("test")
public class ApplicationUserDtoSerializer extends JsonSerializer<ApplicationUserDto> {

    @Override
    public void serialize(ApplicationUserDto value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("email", value.email());
        gen.writeStringField("password", value.password());
        gen.writeStringField("role", value.role().name());
        gen.writeEndObject();
    }
}