package com.garrettw011.orderflow.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderSummaryResponse(
        @Schema(description = "order id", example = "42")
        Long id,

        @Schema(example = "RESERVED",
                allowableValues = {"RESERVED","PAID","FULFILLING","SHIPPED","CANCELED","FAILED"})
        String status,

        @Schema(example = "179.98")
        BigDecimal subtotal,

        @Schema(example = "14.40")
        BigDecimal tax,

        @Schema(example = "194.38")
        BigDecimal total,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant createdAt
) {}

