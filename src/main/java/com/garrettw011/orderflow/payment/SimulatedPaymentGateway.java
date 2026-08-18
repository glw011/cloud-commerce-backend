package com.garrettw011.orderflow.payment;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class SimulatedPaymentGateway implements PaymentGateway {
    // simulate processor server-side idempotency
    private final Map<String, PaymentResult> processed = new ConcurrentHashMap<>();

    @Override
    public PaymentResult charge(BigDecimal amount, String paymentToken, String idempotencyKey) {
        return processed.computeIfAbsent(idempotencyKey, k -> {
            if (paymentToken.startsWith("fail")) { return new PaymentResult(false, null); }
            return new PaymentResult(true, "txn_" + UUID.randomUUID());
        });
    }
}
