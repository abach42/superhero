package com.abach42.superhero.shared.convertion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Converter
@Component
public class EncryptionConverter implements AttributeConverter<String, String> {
    final
    EncryptionHelper encryptionHelper;

    public EncryptionConverter(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return encryptionHelper.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return encryptionHelper.decrypt(dbData);
    }
}
