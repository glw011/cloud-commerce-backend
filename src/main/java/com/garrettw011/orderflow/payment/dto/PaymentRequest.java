package com.garrettw011.orderflow.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(
        @Schema(description = "Payment provider name", example = "stripe")
        @NotBlank String provider,

        @Schema(description = "Opaque payment method token. Simulated gateway declines tokens starting" +
                "with \"fail\" and captures everything else.", example = "tok_visa")
        @NotBlank String paymentToken
) {}
