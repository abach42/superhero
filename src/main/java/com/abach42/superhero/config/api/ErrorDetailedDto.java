package com.abach42.superhero.config.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema(name = "error detail")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetailedDto extends ErrorDto {

    private List<ValidationError> errors;

    public ErrorDetailedDto(Integer status, String error, String message, String path) {
        super(status, error, message, path);
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

    @Schema(name = "validation errors")
    public record ValidationError(
            @Schema(
                    title = "field",
                    example = "name"
            )
            String field,
            @Schema(
                    title = "message",
                    example = "Name is missing"
            )
            String message) {

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        @Override
        public String field() {
            return field;
        }

        @Override
        public String message() {
            return message;
        }
    }
}
