package com.abach42.superhero.config.convertion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class GenericEnumConverter<E extends Enum<E> & ConvertibleEnum<T>, T>
        implements AttributeConverter<E, T> {

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public abstract E convertToEntityAttribute(T dbData);
}