package com.garrettw011.orderflow.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LowStockInventoryResponse(
        @Schema(example = "3")
        Long productId,

        @Schema(example = "MXS-CHR-ERG-001")
        String sku,

        @Schema(example = "MaxSit: Ergonomic Chair")
        String name,

        @Schema(example = "100")
        int quantityOnHand,

        @Schema(example = "75")
        int availableQuantity,

        @Schema(example = "5")
        int reorderThreshold
) {}
