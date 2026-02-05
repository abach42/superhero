package com.abach42.superhero.shared.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "error")
public class ErrorDto {

    @Schema(
            title = "status",
            description = "http status number",
            example = "666",
            format = "integer"
    )
    @JsonProperty("status")
    protected final Integer status;

    @Schema(
            title = "message",
            example = "Something really impossible happened."
    )
    @JsonProperty("message")
    protected final String message;

    @Schema(
            title = "path",
            example = "/api/v1/myentity/777"
    )
    @JsonProperty("path")
    protected final String path;

    @Schema(
            title = "error",
            example = "Impossible status"
    )
    @JsonProperty("error")
    private final String error;

    public ErrorDto(
            @JsonProperty("status") Integer status,
            @JsonProperty("error") String error,
            @JsonProperty("message") String message,
            @JsonProperty("path") String path) {
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