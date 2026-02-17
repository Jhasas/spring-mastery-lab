package com.spring_base.fundamentals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleCustomerNotFound(CustomerNotFoundException ex) {
        return Map.of(
                "error", "Not Found",
                "message", ex.getMessage(),
                "status", HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidationError(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(field -> field.getField() + ": " + field.getDefaultMessage())
                .toList();
        return Map.of(
                "error", "Bad Request",
                "message", errors,
                "status", HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleRuntimeException(RuntimeException ex) {
        return Map.of(
                "error", "Internal Server Error",
                "message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error",
                "status", HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
