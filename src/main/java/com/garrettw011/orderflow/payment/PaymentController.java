package com.garrettw011.orderflow.payment;

import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.OrderProcessingManager;
import com.garrettw011.orderflow.payment.dto.PaymentRequest;
import com.garrettw011.orderflow.payment.dto.PaymentResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/payments")
public class PaymentController {
    private final OrderProcessingManager orderManager;

    public PaymentController(OrderProcessingManager orderManager) { this.orderManager = orderManager; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest req) {
        return orderManager.pay(SecurityUtils.currentUserId(), orderId, req, isAdmin());
    }

    @GetMapping
    public List<PaymentResponse> list(@PathVariable Long orderId) {
        return orderManager.listPayments(SecurityUtils.currentUserId(), orderId, isAdmin());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

