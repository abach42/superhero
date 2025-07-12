package com.abach42.superhero.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.config.api.ErrorDetailedDto;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @Test
    @DisplayName("should handle MethodArgumentNotValidException with validation errors")
    void shouldHandleMethodArgumentNotValidExceptionWithValidationErrors() {
        String requestPath = "/api/users";
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        FieldError fieldError1 = new FieldError(
                "user", "email", "Email is required");
        FieldError fieldError2 = new FieldError(
                "user", "password", "Password must be at least 8 characters");

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);
        given(bindingResult.getFieldErrors()).willReturn(java.util.List.of(fieldError1, fieldError2));

        ResponseEntity<ErrorDetailedDto> result = subject.handleMethodArgumentNotValid(
                exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(422);
        assertThat(result.getBody().getError()).isEqualTo("Unprocessable Entity");
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
        assertThat(result.getBody().getErrors()).hasSize(2);
        assertThat(result.getBody().getErrors().get(0).field()).isEqualTo("email");
        assertThat(result.getBody().getErrors().get(0).message()).isEqualTo(
                "Email is required");
        assertThat(result.getBody().getErrors().get(1).field()).isEqualTo("password");
        assertThat(result.getBody().getErrors().get(1).message()).isEqualTo(
                "Password must be at least 8 characters");
    }

    @Test
    @DisplayName("should handle MethodArgumentNotValidException with no validation errors")
    void shouldHandleMethodArgumentNotValidExceptionWithNoValidationErrors() {
        String requestPath = "/api/users";
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null,
                bindingResult);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);
        given(bindingResult.getFieldErrors()).willReturn(java.util.List.of());

        ResponseEntity<ErrorDetailedDto> result = subject.handleMethodArgumentNotValid(exception,
                servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(422);
        assertThat(result.getBody().getError()).isEqualTo("Unprocessable Entity");
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
        assertThat(result.getBody().getErrors()).isNull();
    }

    @Test
    @DisplayName("should handle ApiException with BAD_REQUEST status")
    void shouldHandleApiExceptionWithBadRequestStatus() {
        String exceptionMessage = "Invalid request data";
        String requestPath = "/api/test";
        ApiException exception = new ApiException(HttpStatus.BAD_REQUEST, exceptionMessage);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);

        ResponseEntity<ErrorDto> result = subject.handle(exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(400);
        assertThat(result.getBody().getError()).isEqualTo("Bad Request");
        assertThat(result.getBody().getMessage()).isEqualTo(exceptionMessage);
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
    }

    @Test
    @DisplayName("should handle ApiException with NOT_FOUND status")
    void shouldHandleApiExceptionWithNotFoundStatus() {
        String exceptionMessage = "Resource not found";
        String requestPath = "/api/users/999";
        ApiException exception = new ApiException(HttpStatus.NOT_FOUND, exceptionMessage);

        given(servletWebRequest.getRequest()).willReturn(httpServletRequest);
        given(httpServletRequest.getRequestURI()).willReturn(requestPath);

        ResponseEntity<ErrorDto> result = subject.handle(exception, servletWebRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(404);
        assertThat(result.getBody().getError()).isEqualTo("Not Found");
        assertThat(result.getBody().getMessage()).isEqualTo(exceptionMessage);
        assertThat(result.getBody().getPath()).isEqualTo(requestPath);
    }
}