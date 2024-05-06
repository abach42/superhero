package com.abach42.superhero.configuration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.abach42.superhero.configuration.serialization.SuperheroDtoSerializer;
import com.abach42.superhero.configuration.serialization.UserDtoSerializer;
import com.abach42.superhero.dto.SuperheroDto;
import com.abach42.superhero.dto.SuperheroUserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Profile("test")
@Component
public class ObjectMapperSerializerHelper {

    public ObjectMapper get() {
        SimpleModule simpleModule = new SimpleModule()
                .addSerializer(SuperheroDto.class, new SuperheroDtoSerializer())
                .addSerializer(SuperheroUserDto.class, new UserDtoSerializer());
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.ALWAYS).registerModule(simpleModule);
    }
}
