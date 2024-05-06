package com.abach42.superhero.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "error detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetailedDto extends ErrorDto {
    private List<ValidationError> errors;

    public ErrorDetailedDto(Integer status, String error, String message, String path) {
        super(status, error, message, path);
    }

    @Schema(name = "validation errors")
    private static class ValidationError {
        @Schema(title = "field", example = "name")
        private final String field;
        @Schema(title = "message", example = "Name is missing")
        private final String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        //used by spring framework, vscode does not see it
        @SuppressWarnings("unused")
        public String getField() {
            return field;
        }

        //used by spring framework, vscode does not see it
        @SuppressWarnings("unused")
        public String getMessage() {
            return message;
        }
    }

    public List<ValidationError> getErrors() {
        return errors;
    }  

    public void addValidationError(String field, String message) {
        if (Objects.isNull(errors)) {
            errors = new ArrayList<>();
        }
        errors.add(new ValidationError(field, message));
    }
}
