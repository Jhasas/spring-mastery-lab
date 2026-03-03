package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.service.customer.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    // POST tests

    @Test
    @DisplayName("POST: should return 201 when creating customer successfully")
    void shouldReturn201WhenCreatingCustomer() throws Exception {
        // ARRANGE
        Customer created = new Customer(1L, "Lucas", "lucas@email.com");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(created);

        // ACT + ASSERT
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas\", \"email\": \"lucas@email.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Lucas"))
                .andExpect(jsonPath("$.email").value("lucas@email.com"));

        verify(customerService).createCustomer(any(Customer.class));
    }

    @Test
    @DisplayName("POST: should return 400 when name is blank")
    void shouldReturn400WhenPostNameIsBlank() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"email\": \"lucas@email.com\"}"))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    @Test
    @DisplayName("POST: should return 400 when email is invalid")
    void shouldReturn400WhenPostEmailIsInvalid() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas\", \"email\": \"invalid\"}"))
                .andExpect(status().isBadRequest());

        verify(customerService, never()).createCustomer(any());
    }

    // PATCH tests

    @Test
    @DisplayName("PATCH: should return 200 when update is successful")
    void shouldReturn200WhenPatchSucceeds() throws Exception {
        // ARRANGE
        Customer updated = new Customer(1L, "Lucas Teste", "lucas@email.com");

        when(customerService.updateCustomer(eq(1L), any(Customer.class)))
                .thenReturn(Map.of("customer", updated, "elapsedMs", 0L, "method", "updateCustomer"));

        // ACT + ASSERT
        mockMvc.perform(patch("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas Teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.name").value("Lucas Teste"))
                .andExpect(jsonPath("$.customer.email").value("lucas@email.com"));

        verify(customerService).updateCustomer(eq(1L), any(Customer.class));
    }

    @Test
    @DisplayName("PATCH: should return 404 when customer does not exist")
    void shouldReturn404WhenCustomerDoesNotExist() throws Exception {
        // ARRANGE
        when(customerService.updateCustomer(eq(99L), any(Customer.class)))
                .thenThrow(new CustomerNotFoundException(99L));

        // ACT + ASSERT
        mockMvc.perform(patch("/customer/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas Teste\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 99"));
    }

    @Test
    @DisplayName("PUT: should return 200 when PUT with Idempotency-Key")
    void shouldReturn200WhenPutWithIdempotencyKey() throws Exception {
        // ARRANGE
        Customer updated = new Customer(1L, "Lucas Teste", "lucas@email.com");

        when(customerService.replaceCustomer(anyString(), eq(1L), any(Customer.class)))
                .thenReturn(Map.of("customer", updated, "elapsedMs", 0L, "method", "updateCustomer"));

        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .header("Idempotency-Key", "key-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas Teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.name").value("Lucas Teste"))
                .andExpect(jsonPath("$.customer.email").value("lucas@email.com"));
    }

    @Test
    @DisplayName("PUT: should accept PUT without Idempotency-Key")
    void shouldAcceptPutWithoutIdempotencyKey() throws Exception {
        // ARRANGE
        Customer updated = new Customer(1L, "Lucas Teste", "lucas@email.com");

        when(customerService.replaceCustomer(any(), eq(1L), any(Customer.class)))
                .thenReturn(Map.of("customer", updated, "elapsedMs", 0L, "method", "updateCustomer"));

        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas Teste\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.name").value("Lucas Teste"))
                .andExpect(jsonPath("$.customer.email").value("lucas@email.com"));
    }
    @Test
    @DisplayName("PUT: should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"email\": \"lucas@email.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(customerService, never()).replaceCustomer(any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "@.com", "user@", "abc"})
    @DisplayName("PUT: should return 400 when email is invalid")
    void shouldReturn400WhenEmailIsInvalid(String invalidEmail) throws Exception {
        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas\", \"email\": \"" + invalidEmail + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("GET: should return 200 and customer when id exists")
    void shouldReturn200AndCustomerWhenGetWithExistingId() throws Exception {
        // ARRANGE
        Customer customer = new Customer(1L, "Lucas Teste", "lucasteste@email.com");

        when(customerService.getCustomer(eq(1L)))
                .thenReturn(Map.of("customer", customer, "elapsedMs", 0L, "method", "getCustomer"));

        // ACT + ASSERT
        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.name").value("Lucas Teste"))
                .andExpect(jsonPath("$.customer.email").value("lucasteste@email.com"));
    }

    @Test
    @DisplayName("GET: should return 404 when id does not exist")
    void shouldReturn404WhenGetWithNonExistentId() throws Exception {
        // ARRANGE
        when(customerService.getCustomer(eq(42L)))
                .thenThrow(new CustomerNotFoundException(42L));

        // ACT + ASSERT
        mockMvc.perform(get("/customer/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 42"));

    }

    @Test
    @DisplayName("GET: should return 200 with customer list")
    void shouldReturn200WithCustomerList() throws Exception {
        // ARRANGE
        List<Customer> customers = List.of(
                new Customer(1L, "Lucas", "lucas@email.com"),
                new Customer(2L, "Maria", "maria@email.com")
        );

        when(customerService.getAllCustomers()).thenReturn(customers);

        // ACT + ASSERT
        mockMvc.perform(get("/customer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Lucas"))
                .andExpect(jsonPath("$[1].name").value("Maria"));

        verify(customerService).getAllCustomers();

    }

    @Test
    @DisplayName("DELETE: should return 200 when delete is successful")
    void shouldReturn200WhenDeleteSucceeds() throws Exception {

        // ARRANGE
        Customer customer = new Customer(1L, "Lucas Teste", "lucasteste@email.com");

        when(customerService.deleteCustomer(eq(1L)))
                .thenReturn(customer);

        // ACT + ASSERT
        mockMvc.perform(delete("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lucas Teste"));

        verify(customerService).deleteCustomer(eq(1L));

    }

    @Test
    @DisplayName("DELETE: should return 404 when deleting non-existent id")
    void shouldReturn404WhenDeletingNonExistentId() throws Exception {

        // ARRANGE
        when(customerService.deleteCustomer(eq(42L)))
                .thenThrow(new CustomerNotFoundException(42L));

        // ACT + ASSERT
        mockMvc.perform(delete("/customer/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 42"));

    }

}
