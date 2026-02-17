package com.spring_base.fundamentals.controller;

import com.spring_base.fundamentals.model.Customer;
import com.spring_base.fundamentals.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PutMapping("/{id}")
    public Map<String, Object> putCustomer(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey, @PathVariable Long id, @RequestBody Customer customer) {
        return customerService.replaceCustomer(idempotencyKey, id, customer);
    }

    @PatchMapping("/{id}")
    public Map<String, Object> patchCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

}
