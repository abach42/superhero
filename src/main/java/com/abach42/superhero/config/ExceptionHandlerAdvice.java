package com.abach42.superhero.config;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

import com.abach42.superhero.entity.dto.ErrorDetailedDto;
import com.abach42.superhero.entity.dto.ErrorDto;
import com.abach42.superhero.exception.ApiException;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorDetailedDto> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
            ServletWebRequest request) {
        HttpStatus httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
        ErrorDetailedDto errorDetailedResponse = new ErrorDetailedDto(getStatusCodeNumber(httpStatus), getError(httpStatus), 
                getMessage(exception), getPath(request));
    
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            errorDetailedResponse.addValidationError(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(errorDetailedResponse);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorDto> handle(ApiException exception, ServletWebRequest request) {
        return new ResponseEntity<>(
            new ErrorDto(getStatusCodeNumber(exception.getStatusCode()), getError(exception), getMessage(exception), getPath(request)),         
                exception.getStatusCode());
    }

    private int getStatusCodeNumber(HttpStatusCode httpStatus) {
        return httpStatus.value();
    }

    private String getError(ErrorResponse exception) {
        return Optional.of(HttpStatus.resolve(getStatusCodeNumber(exception.getStatusCode())))
                .map(HttpStatus::getReasonPhrase).orElse("Unknown error");
    }

    private String getError(HttpStatus httpStatus) {
        return httpStatus.getReasonPhrase();
    }

    private String getMessage(ErrorResponse exception) {
        return exception.getBody().getDetail();
    }

    private String getPath(ServletWebRequest request) {
        return request.getRequest().getRequestURI().toString();
    }
}