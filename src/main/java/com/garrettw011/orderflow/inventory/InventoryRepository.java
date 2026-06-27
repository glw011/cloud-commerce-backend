package com.garrettw011.orderflow.inventory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    @EntityGraph(attributePaths = "product")
    Optional<InventoryItem> findByProductId(Long productId);

    @EntityGraph(attributePaths = "product")
    @Override
    Page<InventoryItem> findAll(Pageable pageable);

    @EntityGraph(attributePaths = "product")
    @Query("""
           SELECT i FROM InventoryItem i
           WHERE i.reorderThreshold > 0
                AND (i.quantityOnHand - i.quantityReserved) <= i.reorderThreshold""")
    List<InventoryItem> findLowStock();
}
