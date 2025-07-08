package com.abach42.superhero.login.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the response containing authentication tokens.
 * <p>
 * <a href="https://datatracker.ietf.org/doc/html/rfc6750#section-4">rfc6750</a>
 * </p>
 *
 * <p>
 * Typically, a bearer token is returned to the client as part of an OAuth 2.0 [RFC6749] access
 * token response.  An example of such a response is:
 * </p>
 *
 * <p>
 * HTTP/1.1 200 OK Content-Type: application/json;charset=UTF-8 Cache-Control: no-store Pragma:
 * no-cache
 * <p>
 *
 * <pre>
 * {@code
 *   {
 *    "access_token":"mF_9.B5f-4.1JqM",
 *    "token_type":"Bearer",
 *    "expires_in":3600, //seconds by rfc6750 - incc internally handles in minutes for simpler configuration
 *    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA"
 *   }
 * }
 * </pre>
 * <p>
 * - An access token. - The type of the token. - The duration in seconds before the token expires. -
 * A refresh token.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public record TokenResponseDto(
        String access_token,
        TokenType token_type,
        int expires_in,
        String refresh_token
) {

    public enum TokenType {
        BEARER("Bearer");

        private String type = "";

        TokenType(String type) {
            this.type = type;
        }

        @JsonValue
        public String getType() {
            return type;
        }

        @Override
        public String toString() {
            return type;
        }
    }
}