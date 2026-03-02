package com.spring_base.fundamentals.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should return Map with correct message")
    void deveRetornarMapComMensagemCorreta() {

        //ARRANGE
        CustomerNotFoundException exception = new CustomerNotFoundException(42L);

        // ACT
        Map<String, Object> response = globalExceptionHandler.handleCustomerNotFound(exception);

        // ASSERT
        assertEquals("Not Found", response.get("error"));
        assertEquals("Customer not found: 42", response.get("message"));

    }

    @Test
    @DisplayName("Should contain NOT_FOUND status")
    void deveConterStatusNotFound() {
        //ARRANGE
        CustomerNotFoundException exception = new CustomerNotFoundException(42L);

        // ACT
        Map<String, Object> response = globalExceptionHandler.handleCustomerNotFound(exception);

        // ASSERT
        assertEquals(HttpStatus.NOT_FOUND, response.get("status"));
    }

}
