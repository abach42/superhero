package com.abach42.superhero.shared.convertion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.function.Consumer;

@JsonDeserialize(using = PatchFieldDeserializer.class)
@JsonSerialize(using = PatchFieldSerializer.class)
public sealed interface PatchField<T> permits PatchField.Missing, PatchField.Value {

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

    boolean isPresent();

    T value();

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
