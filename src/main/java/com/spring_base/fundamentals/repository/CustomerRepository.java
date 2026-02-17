package com.spring_base.fundamentals.repository;

import com.spring_base.fundamentals.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
