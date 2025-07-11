package com.abach42.superhero.user;

import com.abach42.superhero.config.convertion.GenericEnumConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter extends GenericEnumConverter<UserRole, Byte> {

    @Override
    public UserRole convertToEntityAttribute(Byte dbData) {
        if (dbData == null) {
            return null;
        }

        return fromValue(UserRole.class, dbData);
    }
}