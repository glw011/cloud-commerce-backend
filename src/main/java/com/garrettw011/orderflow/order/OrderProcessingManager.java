package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.metrics.BusinessMetrics;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.payment.dto.PaymentRequest;
import com.garrettw011.orderflow.payment.dto.PaymentResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderProcessingManager {
    private final OrderProcessingService core;
    private final BusinessMetrics metrics;

    public OrderProcessingManager(OrderProcessingService core, BusinessMetrics metrics) {
        this.core = core;
        this.metrics = metrics;
    }

    @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 3, delay = 25, multiplier = 2.0)
    public PaymentResponse pay(Long userId, Long orderId, PaymentRequest req, boolean isAdmin) {
        PaymentResponse resp = core.pay(userId, orderId, req, isAdmin);         // only returns once payment commits

        // record payment based on outcome (post-commit)
        if (resp.status().equals("CAPTURED")) { metrics.paymentCaptured(); }
        else { metrics.paymentFailed(); }

        return resp;
    }

    @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 3, delay = 25, multiplier = 2.0)
    public OrderResponse cancel(Long userId, Long orderId, boolean isAdmin) {
        return core.cancel(userId, orderId, isAdmin);
    }

    public OrderResponse fulfill(Long orderId) {
        return core.startFulfillment(orderId);
    }

    public OrderResponse ship(Long orderId) {
        return core.ship(orderId);
    }

    public List<PaymentResponse> listPayments(Long userId, Long orderId, boolean isAdmin) {
        return core.listPayments(userId, orderId, isAdmin);
    }
}

