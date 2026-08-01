package com.garrettw011.orderflow.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;

public record ProductUpdateRequest(
        @Schema(example = "MaxSit: Ergonomic Chair")
        @NotBlank String name,

        @Schema(description = "optional product description (nullable)", example = "MaxSit's adjustable ergonomic chair with lumbar support")
        @Nullable String description,

        @Schema(description = "sales price per unit (must be greater than 0)", example = "299.99",
                minimum = "0", exclusiveMinimum = true)
        @NotNull @Positive BigDecimal price,

        @Schema(description = "whether product is available for sales", example = "true")
        @NotNull Boolean active
) {}
