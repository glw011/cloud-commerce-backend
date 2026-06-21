package com.garrettw011.orderflow.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByProductId(Long productId);
    List<InventoryItem> findByQuantityOnHandLessThanEqual(int threshold);
}

