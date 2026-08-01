package com.garrettw011.orderflow.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        @Schema(description = "order id", example = "42")
        Long id,

        @Schema(description = "id of customer who owns order", example = "3")
        Long customerId,

        @Schema(description = "current status of this order",
                allowableValues = {"RESERVED","PAID","FULFILLING","SHIPPED","CANCELED","FAILED"},
                example = "RESERVED")
        String status,

        @Schema(description = "sum of order item line totals", example = "179.98")
        BigDecimal subtotal,

        @Schema(description = "computed tax", example = "14.40")
        BigDecimal tax,

        @Schema(description = "subtotal plus tax", example = "194.38")
        BigDecimal total,

        @Schema(description = "list of order items")
        List<OrderItemResponse> items,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant createdAt,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant updatedAt
) {}


