package com.abach42.superhero.entity.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Schema(
        title = "HttpStatus", 
        example = "400",
        format = "integer"
    )
    private final int status;

    @Schema(
        title = "message", 
        example = "Something impossible"
    )
    private final String message;
    private List<ValidationError> errors;

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    private static class ValidationError {
        @Schema(
            title = "field", 
            example = "name"
        )
        private final String field;
        @Schema(
            title = "message", 
            example = "Name is missing"
        )
        private final String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}
