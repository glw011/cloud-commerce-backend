package com.garrettw011.orderflow.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        @Schema(description = "payment id", example = "5")
        Long id,

        @Schema(description = "order that payment is for", example = "42")
        Long orderId,

        @Schema(example = "stripe")
        String provider,

        @Schema(description = "payment status",
                allowableValues = {"PENDING", "AUTHORIZED", "CAPTURED", "FAILED", "REFUNDED"},
                example = "CAPTURED")
        String status,

        @Schema(description = "amount charged", example = "194.38")
        BigDecimal amount,

        @Schema(description = "gateway transaction ref that is null on decline (nullable)",
                example = "txn_9f2c7a10-4e6b-4c21-8b0e-2f1d3a5c7e90")
        @Nullable String transactionReference,

        @Schema(description = "order status after this payment",
                allowableValues = {"RESERVED","PAID","FULFILLING","SHIPPED","CANCELED","FAILED"},
                example = "PAID")
        String orderStatus,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant createdAt
) {}

