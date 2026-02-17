package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import java.util.Map;

public class CustomerServiceTest {

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService();
    }

    @Test
    @DisplayName("PATCH: deve atualizar apenas o nome quando email é null")
    void deveAtualizarApenasNomeQuandoEmailNull() {

        //ARRANGE
        Customer partialData = new Customer(null, "Lucas Test", null);

        // ACT
        Map<String, Object> response = customerService.updateCustomer(1L, partialData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals("Lucas Test", result.getName());
        assertEquals("lucas@email.com", result.getEmail());
    }

    @Test
    @DisplayName("PATCH: deve atualizar apenas o email quando nome é null")
    void deveAtualizarApenasEmailQuandoNomeNull () {
        //ARRANGE
        Customer partialData = new Customer(null, null, "lucasteste@email.com");

        // ACT
        Map<String, Object> response = customerService.updateCustomer(1L, partialData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals("Lucas", result.getName());
        assertEquals("lucasteste@email.com", result.getEmail());
    }

    @Test
    @DisplayName("PATCH: deve dar exception quando o id do usuário não existir")
    void deveLancarExceptionQuandoClienteNaoExiste() {

        //ARRANGE
        Customer partialData = new Customer(null, null, null);

        //ACT
        //ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(99L, partialData);
        });
    }

    @Test
    @DisplayName("PUT: deve substituir customer completamente")
    void deveSubstituirCustomerCompletamente() {

        //ARRANGE
        Customer newData = new Customer(1L, "Lucas Teste", "lucasteste@email.com");

        //ACT
        Map<String, Object> response = customerService.replaceCustomer("key-1", 1L, newData);

        //ASSERT
        Customer result = (Customer) response.get("customer");

        assertEquals(1L, result.getId());
        assertEquals("Lucas Teste", result.getName());
        assertEquals("lucasteste@email.com", result.getEmail());

    }

    @Test
    @DisplayName("PUT: deve retornar resultado anterior quando idempotencyKey for duplicada")
    void deveRetornarResultadoAnteriorQuandoIdempotencyKeyDuplicada() {

        //ARRANGE
        Customer newData = new Customer(1L, "Lucas Teste", "lucasteste@email.com");

        //ACT
        Map<String, Object> response1 = customerService.replaceCustomer("key-1", 1L, newData);
        Map<String, Object> response2 = customerService.replaceCustomer("key-1", 1L, newData);

        //ASSERT
        Customer result1 = (Customer) response1.get("customer");
        Customer result2 = (Customer) response2.get("customer");
        assertSame(result1, result2);

    }

    @Test
    @DisplayName("PUT: deve setar ID do Path no Customer")
    void deveSetarIdDoPathNoCustomer() {

        //ARRANGE
        Customer newData = new Customer(999L, "Lucas Teste", "lucasteste@email.com");

        //ACT
        Map<String, Object> response = customerService.replaceCustomer("key-1", 1L, newData);

        //ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("GET: deve retornar customer quando id existe")
    void deveRetornarCustomerQuandoIdExiste () {
        //ACT
        Map<String, Object> response = customerService.getCustomer(1L);

        //ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals(1L, result.getId());
        assertEquals("Lucas", result.getName());
        assertEquals("lucas@email.com", result.getEmail());

    }

    @Test
    @DisplayName("GET: deve lançar exception quando id não existe")
    void deveLancarExceptionQuandoIdNaoExisteNoGet () {
        //ACT
        //ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomer(42L);
        });
    }

    @Test
    @DisplayName("GET: deve retornar todos os customers")
    void deveRetornarTodosOsCustomers() {
        //ACT
        List<Customer> result = customerService.getAllCustomers();

        //ASSERT
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("DELETE: deve remover customer quando id existe")
    void deveRemoverCustomerQuandoIdExiste() {

        //ACT
        Customer result = customerService.deleteCustomer(1L);

        //ASSERT
        assertEquals("Lucas", result.getName());
        assertEquals(1, customerService.getAllCustomers().size());

    }

    @Test
    @DisplayName("DELETE: deve lançar exception quando deletar id inexistent")
    void deveLancarExceptionQuandoDeletarIdInexistente() {

        //ACT
        //ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(42L);
        });

    }

}
