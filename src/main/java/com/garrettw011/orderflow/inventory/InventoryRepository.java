package com.garrettw011.orderflow.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);

    @Query("""
           SELECT i FROM InventoryItem i
           WHERE i.reorderThreshold > 0
                AND (i.quantityOnHand - i.quantityReserved) <= i.reorderThreshold""")
    List<InventoryItem> findLowStock();
}
