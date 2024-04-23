package com.abach42.superhero.entity.dto;

import com.abach42.superhero.config.serialization.ErrorDtoDeserializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(using = ErrorDtoDeserializer.class)
public class ErrorDto {
    @Schema(title = "HttpStatus", example = "666", format = "integer")
    protected final Integer status;

    @Schema(title = "message", example = "Impossible status")
    private final String error;

    @Schema(title = "message", example = "Something really impossible happened.")
    protected final String message;

    @Schema(title = "path", example = "/api/v1/myentity/777")
    protected final String path;

    public ErrorDto(Integer status, String error, String message, String path) {
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


}
