package com.abach42.superhero.config.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;

@Tags(value = {@Tag("unit"), @Tag("exception")})
public class ErrorDetailedDtoTest {

    @Test
    @DisplayName("Error Detail Dto")
    public void testErrorDetailedDto() {
        Integer status = 400;
        String error = "Bad Request";
        String message = "Validation failed";
        String path = "/api/v1/resource";
        String field = "name";
        String validationMessage = "Name is required";

        ErrorDetailedDto errorDetailedDto = new ErrorDetailedDto(status, error, message, path);
        errorDetailedDto.addValidationError(field, validationMessage);

        assertEquals(status, errorDetailedDto.getStatus());
        assertEquals(error, errorDetailedDto.getError());
        assertEquals(message, errorDetailedDto.getMessage());
        assertEquals(path, errorDetailedDto.getPath());

        var errors = errorDetailedDto.getErrors();
        assertEquals(1, errors.size());
    }
}