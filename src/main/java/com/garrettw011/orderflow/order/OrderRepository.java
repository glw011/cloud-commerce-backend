package com.garrettw011.orderflow.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    // spring ignores word between 'find' & 'By' (just findById with fetch graph)
    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Order> findDetailById(Long id);
}
