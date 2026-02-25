package com.abach42.superhero.shared.convertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@Converter
public class EncryptedStringListConverter implements AttributeConverter<List<String>, String> {

    private final EncryptionHelper encryptionHelper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EncryptedStringListConverter(EncryptionHelper encryptionHelper) {
        this.encryptionHelper = encryptionHelper;
    }

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(attribute);
            return encryptionHelper.encrypt(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting list to encrypted JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            String decryptedJson = encryptionHelper.decrypt(dbData);
            return objectMapper.readValue(decryptedJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting encrypted JSON to list", e);
        }
    }
}