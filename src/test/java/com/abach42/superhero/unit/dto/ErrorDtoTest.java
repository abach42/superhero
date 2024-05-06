package com.abach42.superhero.unit.dto;

import org.junit.jupiter.api.Test;

import com.abach42.superhero.dto.ErrorDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;

public class ErrorDtoTest {

    @Test
    @DisplayName("Error Dto")
    public void testErrorDto() {
        Integer status = 404;
        String error = "Not Found";
        String message = "Resource not found";
        String path = "/api/v1/resource/123";

        ErrorDto errorDto = new ErrorDto(status, error, message, path);

        assertEquals(status, errorDto.getStatus());
        assertEquals(error, errorDto.getError());
        assertEquals(message, errorDto.getMessage());
        assertEquals(path, errorDto.getPath());
    }
}
