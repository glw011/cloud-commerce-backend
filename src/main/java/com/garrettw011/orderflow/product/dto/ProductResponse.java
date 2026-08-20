package com.garrettw011.orderflow.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        @Schema(description = "Product id number", example = "3")
        Long id,

        @Schema(example = "MXS-CHR-ERG-001")
        String sku,

        @Schema(example = "MaxSit: Ergonomic Chair")
        String name,

        @Schema(example = "MaxSit's adjustable ergonomic chair with lumbar support")
        @Nullable String description,

        @Schema(example = "299.99")
        BigDecimal price,

        @Schema(example = "true")
        boolean active,

        @Schema(description = "timestamp when product was created", example = "2026-07-04T00:33:06.134435Z")
        Instant createdAt,

        @Schema(description = "timestamp when product was last updated", example = "2026-07-04T00:33:06.134435Z")
        Instant updatedAt
) {}

