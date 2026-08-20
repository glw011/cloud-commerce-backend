package com.garrettw011.orderflow.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record InventoryResponse(
        @Schema(description = "inventory record id", example = "3")
        Long id,

        @Schema(description = "product id for product this inventory tracks", example = "3")
        Long productId,

        @Schema(example = "MXS-CHR-ERG-001")
        String sku,

        @Schema(description = "total units in stock", example = "100")
        int quantityOnHand,

        @Schema(description = "number of units held for open orders", example = "4")
        int quantityReserved,

        @Schema(description = "number of units available for sale (on-hand - reserved)", example = "96")
        int availableQuantity,

        @Schema(description = "quantity that triggers low-stock", example = "10")
        int reorderThreshold,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant updatedAt

) {}
