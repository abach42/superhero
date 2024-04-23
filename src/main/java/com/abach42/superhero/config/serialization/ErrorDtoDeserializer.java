package com.abach42.superhero.config.serialization;

import java.io.IOException;

import com.abach42.superhero.entity.dto.ErrorDto;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class ErrorDtoDeserializer extends JsonDeserializer<ErrorDto> {
    @Override
    public ErrorDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);

        final Integer status = node.get("status").asInt();
        final String error = node.get("error").asText();
        final String message = node.get("message").asText();
        final String path = node.get("path").asText();

        return new ErrorDto(status, error, message, path);
    }
}
