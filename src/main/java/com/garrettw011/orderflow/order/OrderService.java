package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.exception.ResourceNotFoundException;
import com.garrettw011.orderflow.common.exception.DuplicateResourceException;
import com.garrettw011.orderflow.common.idempotency.IdempotencyService;
import com.garrettw011.orderflow.customer.Customer;
import com.garrettw011.orderflow.customer.CustomerService;
import com.garrettw011.orderflow.order.dto.OrderCreateRequest;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.order.dto.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jspecify.annotations.Nullable;
import java.util.Optional;

@Service
public class OrderService {
    private final RetryOrderCreator orderCreator;
    private final OrderCreationService creationService;
    private final OrderRepository orders;
    private final CustomerService customerService;
    private final IdempotencyService idempotency;

    public OrderService(RetryOrderCreator orderCreator, OrderCreationService creationService, OrderRepository orders,
                        CustomerService customerService, IdempotencyService idempotency) {
        this.orderCreator = orderCreator;
        this.creationService = creationService;
        this.orders = orders;
        this.customerService = customerService;
        this.idempotency = idempotency;
    }

    public OrderResponse placeOrder(Long userId, OrderCreateRequest req, @Nullable String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) { return orderCreator.create(userId, req); }

        String scope = "order:" + userId;

        Optional<String> existing = idempotency.find(scope, idempotencyKey);
        if (existing.isPresent()) {
            if (IdempotencyService.PROCESSING.equals(existing.get())) {
                throw new DuplicateResourceException("Request is already in progress...");
            }
            // return already created order
            return loadDetail(Long.valueOf(existing.get()));
        }

        if (!idempotency.claim(scope, idempotencyKey)) {
            throw new DuplicateResourceException("Request is already in progress...");
        }

        try {
            OrderResponse created = orderCreator.create(userId, req);
            idempotency.store(scope, idempotencyKey, String.valueOf(created.id()));
            return created;
        }
        catch (RuntimeException ex) {
            idempotency.release(scope, idempotencyKey);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> listMyOrders(Long userId, Pageable pageable) {
        Customer customer = customerService.getByUserId(userId);
        return orders.findByCustomerId(customer.getId(), pageable).map(this::toSummary);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long userId, Long orderId, boolean isAdmin) {
        Order order = orders.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found for order id: " + orderId));

        if (!isAdmin) {
            Customer customer = customerService.getByUserId(userId);
            if (!order.getCustomer().getId().equals(customer.getId())) {
                throw new ResourceNotFoundException("No order found for id: " + orderId);
            }
        }
        return creationService.toResponse(order);
    }

    private OrderResponse loadDetail(Long orderId) {
        Order order = orders.findDetailById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("No order found for id: " + orderId));
        return creationService.toResponse(order);
    }

    private OrderSummaryResponse toSummary(Order o) {
        return new OrderSummaryResponse(o.getId(), o.getStatus().name(), o.getSubtotal(),
                                        o.getTax(), o.getTotal(), o.getCreatedAt());
    }
}




