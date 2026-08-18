package com.garrettw011.orderflow.order.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
        Long id,
        String status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal total,
        Instant createdAt
) {}

