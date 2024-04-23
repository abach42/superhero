package com.abach42.superhero.unit.entity.dto;

import org.junit.jupiter.api.Test;

import com.abach42.superhero.entity.dto.ErrorDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorDtoTest {

    @Test
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
