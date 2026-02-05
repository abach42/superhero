package com.abach42.superhero.shared.convertion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public abstract class GenericEnumConverter<E extends Enum<E> & ConvertibleEnum<T>, T>
        implements AttributeConverter<E, T> {

    @Override
    public T convertToDatabaseColumn(E attribute) {
        if(attribute == null) {
            return null;
        }

        return attribute.getValue();
    }

    @Override
    public abstract E convertToEntityAttribute(T dbData);

    protected E fromValue(Class<E> enumClass, T value) {
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + " value: " + value);
    }
}