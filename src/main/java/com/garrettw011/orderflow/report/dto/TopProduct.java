package com.garrettw011.orderflow.report.dto;

import java.math.BigDecimal;

public record TopProduct(Long productId,
                         String sku,
                         String name,
                         Long unitsSold,
                         BigDecimal revenue
) {}

