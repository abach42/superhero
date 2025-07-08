package com.abach42.superhero.superhero;

import com.abach42.superhero.config.convertion.GenericEnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GenderConverter extends GenericEnumConverter<Gender, Byte> {

    @Override
    public Gender convertToEntityAttribute(Byte dbData) {
        if (dbData == null) {
            return null;
        }

        return fromValue(Gender.class, dbData);
    }
}