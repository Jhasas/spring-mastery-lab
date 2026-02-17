package com.spring_base.fundamentals.service;

import com.spring_base.fundamentals.exception.CustomerNotFoundException;
import com.spring_base.fundamentals.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CustomerService {

    private final Map<Long, Customer> customers = new HashMap<>();
    private final Map<String, Object> idempotencyStore = new HashMap<>();
    public CustomerService() {
        customers.put(1L, new Customer(1L, "Lucas", "lucas@email.com"));
        customers.put(2L, new Customer(2L, "Maria", "maria@email.com"));
    }

    public Map<String, Object> replaceCustomer(String idempotencyKey, Long id, Customer newData) {

        log.info("Replacing Customer for id: {}", id);
        long start = System.currentTimeMillis();

        Customer existingCustomer = customers.get(id);
        Object existingIdempotencyKey = checkIdempotency(idempotencyKey);

        if(existingIdempotencyKey != null) {

            long duration = System.currentTimeMillis() - start;

            return Map.of(
                    "customer", (Customer) existingIdempotencyKey,
                    "elapsedMs", duration,
                    "method", "replaceCustomer"
            );
        }

        if(existingCustomer!=null) {
            storeIdempotency(idempotencyKey, newData);
            this.customers.put(id, newData);
        } else {
            throw new CustomerNotFoundException(id);
        }

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

        Customer existing = customers.get(id);

        if(existing!=null) {
            if(partialData.getName()!= null) existing.setName(partialData.getName());
            if(partialData.getEmail()!= null) existing.setEmail(partialData.getEmail());
        } else {
            throw new CustomerNotFoundException(id);
        }

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

        Object existing = this.idempotencyStore.get(key);

        if(existing != null) {
            log.warn("Idempotency Key duplicated");
            return existing;
        } else {
            return null;
        }

    }

    private void storeIdempotency(String key, Object result) {
        if (key == null) return;

        this.idempotencyStore.put(key, result);
    }

}
