package com.garrettw011.orderflow.inventory.dto;

public record LowStockInventoryResponse(
   Long productId,
   String sku,
   String name,
   int quantityOnHand,
   int availableQuantity,
   int reorderThreshold
) {}
