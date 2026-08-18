package com.garrettw011.orderflow.inventory.dto;

import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

public record InventoryAdjustmentRequest(
        @NotNull Integer delta,
        @Nullable String reason
) {}
