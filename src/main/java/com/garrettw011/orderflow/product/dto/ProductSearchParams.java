package com.garrettw011.orderflow.product.dto;

import org.jspecify.annotations.Nullable;
import java.math.BigDecimal;

public record ProductSearchParams(
        @Nullable Boolean active,
        @Nullable BigDecimal minPrice,
        @Nullable BigDecimal maxPrice,
        @Nullable String name,
        @Nullable String sku
) {}

