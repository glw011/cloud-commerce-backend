package com.garrettw011.orderflow.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record OrderItemResponse(
        @Schema(example = "3")
        Long productId,

        @Schema(example = "KM-KB-MECH-001")
        String sku,

        @Schema(example = "KeyMaster: Mechanical Keyboard")
        String productName,

        @Schema(example = "2")
        int quantity,

        @Schema(description = "price snapshot captured at time of order", example = "89.99")
        BigDecimal unitPrice,

        @Schema(description = "subtotal for this quantity of ordered items (unitPrice * quantity)", example = "179.98")
        BigDecimal lineTotal
) {}



