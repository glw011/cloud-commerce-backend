package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.order.dto.OrderCreateRequest;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RetryOrderCreator {
    private final OrderCreationService creationService;

    public RetryOrderCreator(OrderCreationService creationService) { this.creationService = creationService; }

    @Retryable(includes = OptimisticLockingFailureException.class, maxRetries = 3, delay = 25, multiplier = 2.0)
    public OrderResponse create(Long userId, OrderCreateRequest req) { return creationService.create(userId, req); }
}

