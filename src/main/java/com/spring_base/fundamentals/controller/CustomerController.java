package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PutMapping("/{id}")
    public Map<String, Object> putCustomer(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, @PathVariable Long id, @RequestBody @Valid Customer customer) {
        return customerService.replaceCustomer(idempotencyKey, id, customer);
    }

    @PatchMapping("/{id}")
    public Map<String, Object> patchCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id);
    }

    @GetMapping("")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @DeleteMapping("/{id}")
    public Customer deleteCustomer(@PathVariable Long id) {
        return customerService.deleteCustomer(id);
    }

}
