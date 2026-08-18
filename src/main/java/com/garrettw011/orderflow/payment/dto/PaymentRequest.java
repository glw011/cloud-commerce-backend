package com.garrettw011.orderflow.payment.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentRequest(@NotBlank String provider,
                             @NotBlank String paymentToken) {}
