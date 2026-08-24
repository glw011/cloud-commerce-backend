package com.garrettw011.orderflow.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
    private final Counter ordersPlaced;
    private final Counter paymentsCaptured;
    private final Counter paymentsFailed;

    public BusinessMetrics(MeterRegistry registry) {
        this.ordersPlaced = Counter.builder("orderflow.orders.placed")
                .description("Number of successful orders")
                .register(registry);

        this.paymentsCaptured = Counter.builder("orderflow.payments")
                .description("Payment outcomes")
                .tag("outcome", "captured")
                .register(registry);
        this.paymentsFailed = Counter.builder("orderflow.payments")
                .description("Payment outcomes")
                .tag("outcome", "failed")
                .register(registry);
    }

    public void orderPlaced() { ordersPlaced.increment(); }
    public void paymentCaptured() { paymentsCaptured.increment(); }
    public void paymentFailed() { paymentsFailed.increment(); }
}