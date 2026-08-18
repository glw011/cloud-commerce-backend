package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.dto.OrderCreateRequest;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.order.dto.OrderSummaryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody OrderCreateRequest req,
                                @RequestHeader(value = "Idempotency-Key", required = false)
                                @Nullable String idempotencyKey) {
        return orderService.placeOrder(SecurityUtils.currentUserId(), req, idempotencyKey);
    }

    @GetMapping
    public PageResponse<OrderSummaryResponse> myOrders(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(orderService.listMyOrders(SecurityUtils.currentUserId(), pageable));
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(SecurityUtils.currentUserId(), id, isAdmin());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

