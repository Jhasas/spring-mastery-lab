package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // POST tests

    @Test
    @DisplayName("POST: should return saved customer with generated id")
    void shouldReturnSavedCustomerWithGeneratedId() {
        // ARRANGE
        Customer input = new Customer(null, "Lucas", "lucas@email.com");
        Customer saved = new Customer(1L, "Lucas", "lucas@email.com");
        when(customerRepository.save(input)).thenReturn(saved);

        // ACT
        Customer result = customerService.createCustomer(input);

        // ASSERT
        assertEquals(1L, result.getId());
        assertEquals("Lucas", result.getName());
        assertEquals("lucas@email.com", result.getEmail());
        verify(customerRepository).save(input);
    }

    // PATCH tests

    @Test
    @DisplayName("PATCH: should update only name when email is null")
    void shouldUpdateOnlyNameWhenEmailIsNull() {
        // ARRANGE
        Customer existing = new Customer(1L, "Lucas", "lucas@email.com");
        Customer partialData = new Customer(null, "Lucas Test", null);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        // ACT
        Map<String, Object> response = customerService.updateCustomer(1L, partialData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals("Lucas Test", result.getName());
        assertEquals("lucas@email.com", result.getEmail());
        verify(customerRepository).save(existing);
    }

    @Test
    @DisplayName("PATCH: should update only email when name is null")
    void shouldUpdateOnlyEmailWhenNameIsNull() {
        // ARRANGE
        Customer existing = new Customer(1L, "Lucas", "lucas@email.com");
        Customer partialData = new Customer(null, null, "lucasteste@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(existing);

        // ACT
        Map<String, Object> response = customerService.updateCustomer(1L, partialData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals("Lucas", result.getName());
        assertEquals("lucasteste@email.com", result.getEmail());
        verify(customerRepository).save(existing);
    }

    @Test
    @DisplayName("PATCH: should throw exception when customer id does not exist")
    void shouldThrowExceptionWhenPatchIdNotFound() {
        // ARRANGE
        Customer partialData = new Customer(null, null, null);
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(99L, partialData);
        });
    }

    // PUT tests

    @Test
    @DisplayName("PUT: should replace customer completely")
    void shouldReplaceCustomerCompletely() {
        // ARRANGE
        Customer existing = new Customer(1L, "Lucas", "lucas@email.com");
        Customer newData = new Customer(1L, "Lucas Teste", "lucasteste@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(newData)).thenReturn(newData);

        // ACT
        Map<String, Object> response = customerService.replaceCustomer("key-1", 1L, newData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals(1L, result.getId());
        assertEquals("Lucas Teste", result.getName());
        assertEquals("lucasteste@email.com", result.getEmail());
        verify(customerRepository).save(newData);
    }

    @Test
    @DisplayName("PUT: should return cached result when idempotency key is duplicated")
    void shouldReturnCachedResultWhenIdempotencyKeyDuplicated() {
        // ARRANGE
        Customer existing = new Customer(1L, "Lucas", "lucas@email.com");
        Customer newData = new Customer(1L, "Lucas Teste", "lucasteste@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(newData)).thenReturn(newData);

        // ACT
        Map<String, Object> response1 = customerService.replaceCustomer("key-1", 1L, newData);
        Map<String, Object> response2 = customerService.replaceCustomer("key-1", 1L, newData);

        // ASSERT
        Customer result1 = (Customer) response1.get("customer");
        Customer result2 = (Customer) response2.get("customer");
        assertSame(result1, result2);
    }

    @Test
    @DisplayName("PUT: should set path id on customer")
    void shouldSetPathIdOnCustomer() {
        // ARRANGE
        Customer existing = new Customer(1L, "Lucas", "lucas@email.com");
        Customer newData = new Customer(999L, "Lucas Teste", "lucasteste@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(newData)).thenReturn(newData);

        // ACT
        Map<String, Object> response = customerService.replaceCustomer("key-2", 1L, newData);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("PUT: should throw exception when customer id does not exist")
    void shouldThrowExceptionWhenPutIdNotFound() {
        // ARRANGE
        Customer newData = new Customer(99L, "Lucas Teste", "lucasteste@email.com");
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.replaceCustomer("key-1", 99L, newData);
        });
    }

    // GET tests

    @Test
    @DisplayName("GET: should return customer when id exists")
    void shouldReturnCustomerWhenIdExists() {
        // ARRANGE
        Customer customer = new Customer(1L, "Lucas", "lucas@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // ACT
        Map<String, Object> response = customerService.getCustomer(1L);

        // ASSERT
        Customer result = (Customer) response.get("customer");
        assertEquals(1L, result.getId());
        assertEquals("Lucas", result.getName());
        assertEquals("lucas@email.com", result.getEmail());
        assertNotNull(response.get("elapsedMs"));
        assertEquals("getCustomer", response.get("method"));
    }

    @Test
    @DisplayName("GET: should throw exception when id does not exist")
    void shouldThrowExceptionWhenGetIdNotFound() {
        // ARRANGE
        when(customerRepository.findById(42L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomer(42L);
        });
    }

    @Test
    @DisplayName("GET ALL: should return all customers")
    void shouldReturnAllCustomers() {
        // ARRANGE
        List<Customer> customers = List.of(
                new Customer(1L, "Lucas", "lucas@email.com"),
                new Customer(2L, "Maria", "maria@email.com")
        );
        when(customerRepository.findAll()).thenReturn(customers);

        // ACT
        List<Customer> result = customerService.getAllCustomers();

        // ASSERT
        assertEquals(2, result.size());
    }

    // DELETE tests

    @Test
    @DisplayName("DELETE: should delete and return customer when id exists")
    void shouldDeleteAndReturnCustomerWhenIdExists() {
        // ARRANGE
        Customer customer = new Customer(1L, "Lucas", "lucas@email.com");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        // ACT
        Customer result = customerService.deleteCustomer(1L);

        // ASSERT
        assertEquals("Lucas", result.getName());
        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE: should throw exception when deleting non-existent id")
    void shouldThrowExceptionWhenDeletingNonExistentId() {
        // ARRANGE
        when(customerRepository.findById(42L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.deleteCustomer(42L);
        });
    }
}
