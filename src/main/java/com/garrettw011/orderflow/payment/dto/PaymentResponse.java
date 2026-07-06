package com.garrettw011.orderflow.payment.dto;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(Long id,
                              Long orderId,
                              String provider,
                              String status,
                              BigDecimal amount,
                              @Nullable String transactionReference,
                              String orderStatus,
                              Instant createdAt) {}

