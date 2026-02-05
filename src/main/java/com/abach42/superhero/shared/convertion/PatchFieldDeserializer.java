package com.abach42.superhero.shared.convertion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.io.IOException;

public class PatchFieldDeserializer extends JsonDeserializer<PatchField<?>> implements
        ContextualDeserializer {

    private JavaType valueType;

    @Override
    public PatchField<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // uses inner type (e.g. LocalDate) to parse value
        Object value = ctxt.readValue(p, valueType);
        return PatchField.of(value);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) {
        // extracts generic type T from PatchField<T>
        JavaType wrapperType = property.getType();
        PatchFieldDeserializer deserializer = new PatchFieldDeserializer();
        deserializer.valueType = wrapperType.containedType(0);
        return deserializer;
    }
}
