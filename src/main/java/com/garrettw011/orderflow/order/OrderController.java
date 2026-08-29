package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.PageResponse;
import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.dto.OrderCreateRequest;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import com.garrettw011.orderflow.order.dto.OrderSummaryResponse;
import io.swagger.v3.oas.annotations.media.Content;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name="Orders", description="Place and view orders")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @Operation(summary = "Place an order", description = "Reserves stock and creates 'RESERVED' order. " +
            "Send an idempotency key header to make retries safe.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Validation failed"),
            @ApiResponse(responseCode = "404",
                    description = "Product not found/unavailable or account has no customer profile",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Insufficient stock or a request with same idempotency key is in progress",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @Valid @RequestBody OrderCreateRequest req,
            @Parameter(name = "Idempotency key", description = "Optional key to deduplicate retried submits")
            @RequestHeader(value = "Idempotency-key", required = false)
            @Nullable String idempotencyKey) {
        return orderService.placeOrder(SecurityUtils.currentUserId(), req, idempotencyKey);
    }

    @Operation(summary = "Get list of my orders", description = "Returns list of orders associated with caller.")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Account has no customer profile",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public PageResponse<OrderSummaryResponse> myOrders(
                    @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(orderService.listMyOrders(SecurityUtils.currentUserId(), pageable));
    }

    @Operation(summary = "Get my order with order id", description = "Returns details of user's order " +
            "with an order id. Admins can retrieve any order.")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Order not found or not caller's order (admins bypass ownership check)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

