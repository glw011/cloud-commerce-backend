package com.garrettw011.orderflow.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

public record InventoryAdjustmentRequest(
        @Schema(description = "signed delta value for changing on-hand quantity", example = "50")
        @NotNull Integer delta,

        @Schema(description = "optional note for why adjustment is needed (nullable)",
                example = "Received shipment PO-4471")
        @Nullable String reason
) {}
