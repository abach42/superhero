package com.abach42.superhero.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.config.api.ErrorDto;
import com.abach42.superhero.config.api.ExceptionHandlerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.ServletWebRequest;

@Tags(value = {@Tag("unit"), @Tag("exception")})
@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Handler Advice")
class ExceptionHandlerAdviceTest {

    @Mock
    private ServletWebRequest servletWebRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    private ExceptionHandlerAdvice subject;

    @BeforeEach
    void setUp() {
        subject = new ExceptionHandlerAdvice();
    }

    @Test
    @DisplayName("should handle AccessDeniedException with correct error details")
    void shouldHandleAccessDeniedExceptionWithCorrectErrorDetails() {
        String exceptionMessage = "Access is denied";
        String requestPath = "/api/any/boo";
        AccessDeniedException exception = new AccessDeniedException(exceptionMessage);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);

        ResponseEntity<ErrorDto> result = subject.handleAccessDenied(exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(403);
        assertThat(result.getBody().getError()).isEqualTo("Forbidden");
        assertThat(result.getBody().getMessage()).isEqualTo(exceptionMessage);
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
    }

    @Test
    @DisplayName("should handle AccessDeniedException with null message")
    void shouldHandleAccessDeniedExceptionWithNullMessage() {
        String requestPath = "/api/any/admin";
        AccessDeniedException exception = new AccessDeniedException(null);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);

        ResponseEntity<ErrorDto> result = subject.handleAccessDenied(exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(403);
        assertThat(result.getBody().getError()).isEqualTo("Forbidden");
        assertThat(result.getBody().getMessage()).isNull();
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
    }

    @Test
    @DisplayName("should handle AccessDeniedException with empty message")
    void shouldHandleAccessDeniedExceptionWithEmptyMessage() {
        String exceptionMessage = "";
        String requestPath = "/api/protected";
        AccessDeniedException exception = new AccessDeniedException(exceptionMessage);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);

        ResponseEntity<ErrorDto> result = subject.handleAccessDenied(exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(403);
        assertThat(result.getBody().getError()).isEqualTo("Forbidden");
        assertThat(result.getBody().getMessage()).isEqualTo(exceptionMessage);
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
    }
}