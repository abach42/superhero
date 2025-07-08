package com.abach42.superhero.config.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "error")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

    @Schema(
            title = "status",
            description = "http status number",
            example = "666",
            format = "integer"
    )
    protected final Integer status;
    @Schema(
            title = "message",
            example = "Something really impossible happened."
    )
    protected final String message;
    @Schema(
            title = "path",
            example = "/api/v1/myentity/777"
    )
    protected final String path;
    @Schema(
            title = "message",
            example = "Impossible status"
    )
    private final String error;

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