package com.garrettw011.orderflow.product.dto;

import java.math.BigDecimal;

public record ProductSearchParams(
        Boolean active,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String name,
        String sku
) {}

