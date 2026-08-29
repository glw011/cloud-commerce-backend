package com.garrettw011.orderflow.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @Schema(description = "unique product inventory identifier normalized to uppercase", example = "MXS-CHR-ERG-001")
        @NotBlank String sku,

        @Schema(description = "name displayed to users", example = "MaxSit: Ergonomic Chair")
        @NotBlank String name,

        @Schema(description = "optional description of product (nullable)", example = "MaxSit's adjustable ergonomic chair with lumbar support")
        @Nullable String description,

        @Schema(description = "price of product (must be greater than 0)", example = "299.99",
                minimum = "0", exclusiveMinimum = true)
        @NotNull @Positive BigDecimal price,

        @Schema(description = "whether product is available for sales (nullable, defaults true)", example = "true")
        @Nullable Boolean active
) {}

