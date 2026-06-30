package com.garrettw011.orderflow.product.dto;

import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        @Nullable String description,
        BigDecimal price,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}

