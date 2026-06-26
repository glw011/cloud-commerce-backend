package com.garrettw011.orderflow.inventory.dto;

import jakarta.validation.constraints.NotNull;

public record InventoryAdjustmentRequest(
        @NotNull Integer delta,
        String reason
) {}
