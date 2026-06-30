package com.garrettw011.orderflow.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank String sku,
        @NotBlank String name,
        @Nullable String description,
        @NotNull @Positive BigDecimal price,
        @Nullable Boolean active
) {}

