package com.abach42.superhero.entity.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Schema(title = "HttpStatus", example = "666", format = "integer")
    private final Integer status;

    @Schema(title = "message", example = "Impossible status")
    private final String error;

    @Schema(title = "message", example = "Something really impossible happened.")
    private final String message;

    @Schema(title = "path", example = "/api/v1/myentity/777")
    private final String path;

    private List<ValidationError> errors;

    public ErrorResponse(Integer status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    private static class ValidationError {
        @Schema(title = "field", example = "name")
        private final String field;
        @Schema(title = "message", example = "Name is missing")
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
