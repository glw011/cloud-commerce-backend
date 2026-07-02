package com.garrettw011.orderflow.order.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        String status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {}


