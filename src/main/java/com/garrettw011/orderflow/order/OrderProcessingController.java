package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}")
public class OrderProcessingController {
    private final OrderProcessingManager manager;

    public OrderProcessingController(OrderProcessingManager manager) { this.manager = manager; }

    @PostMapping("/cancel")
    public OrderResponse cancel(@PathVariable Long orderId) {
        return manager.cancel(SecurityUtils.currentUserId(), orderId, isAdmin());
    }

    @PostMapping("/fulfill")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public OrderResponse fulfill(@PathVariable Long orderId) {
        return manager.fulfill(orderId);
    }

    @PostMapping("/ship")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public OrderResponse ship(@PathVariable Long orderId) {
        return manager.ship(orderId);
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
