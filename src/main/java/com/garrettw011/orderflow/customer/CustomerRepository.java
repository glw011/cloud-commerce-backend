package com.garrettw011.orderflow.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @EntityGraph(attributePaths = "user")
    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByUserEmail(String email);

    @EntityGraph(attributePaths = "user")
    @Override
    Optional<Customer> findById(Long id);

    @EntityGraph(attributePaths = "user")
    @Override
    Page<Customer> findAll(Pageable pageable);
}
