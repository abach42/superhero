package com.abach42.superhero.shared.convertion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class PatchFieldSerializer extends JsonSerializer<PatchField<?>> {

    @Override
    public void serialize(PatchField<?> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value.isPresent()) {
            serializers.defaultSerializeValue(value.value(), gen);
        } else {
            // Missing values are typically omitted by Jackson if configured with @JsonInclude(Include.NON_ABSENT)
            // or if the field itself is null in the DTO.
            // Since records don't support @JsonInclude on components easily in all Jackson versions,
            // and we want to avoid serializing "missing" fields at all:
            gen.writeNull();
        }
    }
}
