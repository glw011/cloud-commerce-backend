package com.garrettw011.orderflow.payment;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.OrderProcessingManager;
import com.garrettw011.orderflow.payment.dto.PaymentRequest;
import com.garrettw011.orderflow.payment.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "Payments")
@RestController
@SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
@RequestMapping("/api/v1/orders/{orderId}/payments")
public class PaymentController {
    private final OrderProcessingManager orderManager;

    public PaymentController(OrderProcessingManager orderManager) { this.orderManager = orderManager; }

    @Operation(summary = "Pay for an order", description = "Charges order total and returns CAPTURED payment" +
            "(order -> PAID) or FAILED (order -> FAILED)")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order not found or not caller's",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Order not in RESERVED state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse pay(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest req) {
        return orderManager.pay(SecurityUtils.currentUserId(), orderId, req, isAdmin());
    }

    @Operation(summary = "List an order's payments")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Order not found or not caller's",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

