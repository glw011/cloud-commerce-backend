package com.garrettw011.orderflow.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String sku,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}



