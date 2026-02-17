package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final Map<String, Object> idempotencyStore = new ConcurrentHashMap<>();

    public Customer createCustomer(Customer customer) {
        log.info("Creating new Customer: {}", customer.getName());
        long start = System.currentTimeMillis();

        Customer saved = customerRepository.save(customer);

        long duration = System.currentTimeMillis() - start;
        log.info("Customer created with id {} in {}ms", saved.getId(), duration);

        return saved;
    }

    public Customer deleteCustomer(Long id) {
        log.info("Delete Customer by id: {}", id);
        long start = System.currentTimeMillis();

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.deleteById(id);

        long duration = System.currentTimeMillis() - start;
        log.info("Delete Customer id {} completed in {}ms", id, duration);

        return existingCustomer;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Map<String, Object> getCustomer(Long id) {

        log.info("Get Customer by id: {}", id);
        long start = System.currentTimeMillis();

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        long duration = System.currentTimeMillis() - start;
        log.info("Get Customer id {} completed in {}ms", id, duration);

        return Map.of(
                "customer", existingCustomer,
                "elapsedMs", duration,
                "method", "getCustomer"
        );
    }

    public Map<String, Object> replaceCustomer(String idempotencyKey, Long id, Customer newData) {

        log.info("Replacing Customer for id: {}", id);
        long start = System.currentTimeMillis();

        Object existingIdempotencyKey = checkIdempotency(idempotencyKey);

        if(existingIdempotencyKey != null) {

            long duration = System.currentTimeMillis() - start;

            return Map.of(
                    "customer", (Customer) existingIdempotencyKey,
                    "elapsedMs", duration,
                    "method", "replaceCustomer"
            );
        }

        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        storeIdempotency(idempotencyKey, newData);
        newData.setId(id);
        customerRepository.save(newData);

        long duration = System.currentTimeMillis() - start;
        log.info("Replace for id {} completed in {}ms", id, duration);

        return Map.of(
                "customer", newData,
                "elapsedMs", duration,
                "method", "replaceCustomer"
        );
    }

    public Map<String, Object> updateCustomer(Long id, Customer partialData) {

        log.info("Update Customer for id: {}", id);
        long start = System.currentTimeMillis();

        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        if(partialData.getName()!= null) existing.setName(partialData.getName());
        if(partialData.getEmail()!= null) existing.setEmail(partialData.getEmail());
        customerRepository.save(existing);

        long duration = System.currentTimeMillis() - start;
        log.info("Update for id {} completed in {}ms", id, duration);

        return Map.of(
                "customer", existing,
                "elapsedMs", duration,
                "method", "updateCustomer"
        );
    }

    private Object checkIdempotency(String key) {

        if(key == null) return null;

        Object existing = idempotencyStore.get(key);

        if(existing != null) {
            log.warn("Idempotency Key duplicated");
            return existing;
        } else {
            return null;
        }

    }

    private void storeIdempotency(String key, Object result) {
        if (key == null) return;

        this.idempotencyStore.putIfAbsent(key, result);
    }

}
