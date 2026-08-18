package com.garrettw011.orderflow.inventory.dto;

import java.time.Instant;

public record InventoryResponse(
        Long id,
        Long productId,
        String sku,
        int quantityOnHand,
        int quantityReserved,
        int availableQuantity,
        int reorderThreshold,
        Instant updatedAt

) {}
