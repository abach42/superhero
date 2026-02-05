package com.abach42.superhero.shared.convertion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.function.Consumer;

@JsonDeserialize(using = PatchFieldDeserializer.class)
public sealed interface PatchField<T> permits PatchField.Missing, PatchField.Value {

    boolean isPresent();

    T value();

    static <T> PatchField<T> missing() {
        return new Missing<>();
    }

    @JsonCreator
    static <T> PatchField<T> of(T value) {
        return new Value<>(value);
    }

    static <T> void updateField(PatchField<T> field, Consumer<T> setter) {
        if (field.isPresent()) {
            setter.accept(field.value());
        }
    }

    final class Missing<T> implements PatchField<T> {

        public boolean isPresent() {
            return false;
        }

        public T value() {
            throw new IllegalStateException("Missing has no value");
        }
    }

    record Value<T>(T value) implements PatchField<T> {

        public boolean isPresent() {
            return true;
        }
    }
}
