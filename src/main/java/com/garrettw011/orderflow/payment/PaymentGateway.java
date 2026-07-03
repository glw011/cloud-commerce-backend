package com.garrettw011.orderflow.payment;

import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentResult charge(BigDecimal amount, String paymentToken, String idempotencyKey);
}
