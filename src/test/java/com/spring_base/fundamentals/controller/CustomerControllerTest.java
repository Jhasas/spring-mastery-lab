package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.service.CustomerService;
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

    @Test
    @DisplayName("PATCH: deve retornar 200 quando atualizar com sucesso")
    void deveRetornar200QuandoPatchComSucesso() throws Exception {
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
    @DisplayName("PATCH: deve retornar 404 quando cliente não existe")
    void deveRetornar404QuandoClienteNaoExiste() throws Exception {
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
    @DisplayName("PUT: deve retornar 200 quando PUT com Idempotency-Key")
    void deveRetornar200QuandoPutComIdempotencyKey() throws Exception {
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
    @DisplayName("PUT: deve aceitar PUT sem Idempotency-Key")
    void deveAceitarPutSemIdempotencyKey () throws Exception {
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
    @DisplayName("PUT: deve retornar 400 quando nome em branco")
    void deveRetornar400QuandoNomeEmBranco() throws Exception {
        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\", \"email\": \"lucas@email.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));

        verify(customerService, never()).replaceCustomer(any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"email-invalido", "@.com", "user@", "abc"})
    @DisplayName("PUT: deve retornar 400 quando email inválido")
    void deveRetornar400QuandoEmailInvalido(String invalidEmail) throws Exception {
        // ACT + ASSERT
        mockMvc.perform(put("/customer/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lucas\", \"email\": \"" + invalidEmail + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"));
    }

    @Test
    @DisplayName("GET: deve retornar 200 e customer quando id existe")
    void deveRetornar200ECustomerQuandoGetComIdExistente () throws Exception {
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
    @DisplayName("GET: deve retornar 404 quando id não existe")
    void deveRetornar404QuandoGetComIdInexistente () throws Exception {
        // ARRANGE
        when(customerService.getCustomer(eq(42L)))
                .thenThrow(new CustomerNotFoundException(42L));

        // ACT + ASSERT
        mockMvc.perform(get("/customer/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 42"));

    }

    @Test
    @DisplayName("GET: deve retornar 200 com lista de customers")
    void deveRetornar200ComListaDeCustomers() throws Exception {
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
    @DisplayName("DELETE: deve retornar 200 quando deletar com sucesso")
    void deveRetornar200QuandoDeletarComSucesso() throws Exception {

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
    @DisplayName("DELETE: deve retornar 404 quando deletar id inexistente")
    void deveRetornar404QuandoDeletarIdInexistente() throws Exception {

        // ARRANGE
        when(customerService.deleteCustomer(eq(42L)))
                .thenThrow(new CustomerNotFoundException(42L));

        // ACT + ASSERT
        mockMvc.perform(delete("/customer/42"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found: 42"));

    }

}
