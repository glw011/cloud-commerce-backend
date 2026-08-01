package com.garrettw011.orderflow.order;

import com.garrettw011.orderflow.common.ApiDocs;
import com.garrettw011.orderflow.common.ApiError;
import com.garrettw011.orderflow.common.security.SecurityUtils;
import com.garrettw011.orderflow.order.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Order processing")
@RestController
@SecurityRequirement(name = ApiDocs.BEARER_SCHEME)
@RequestMapping("/api/v1/orders/{orderId}")
public class OrderProcessingController {
    private final OrderProcessingManager manager;

    public OrderProcessingController(OrderProcessingManager manager) { this.manager = manager; }

    @Operation(summary = "Cancel an order", description = "Releases reserved stock and cancels RESERVED order.")
    @ApiResponses({
            @ApiResponse(responseCode = "404",
                    description = "Order not found or not caller's",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Order not in RESERVED state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/cancel")
    public OrderResponse cancel(@PathVariable Long orderId) {
        return manager.cancel(SecurityUtils.currentUserId(), orderId, isAdmin());
    }

    @Operation(summary = "Start order fulfillment", description = "Transitions PAID order to FULFILLING.")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Order not in PAID state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/fulfill")
    @PreAuthorize("hasAnyRole('ADMIN','WAREHOUSE_MANAGER')")
    public OrderResponse fulfill(@PathVariable Long orderId) {
        return manager.fulfill(orderId);
    }

    @Operation(summary = "Ship an order", description = "Transitions FULFILLING order to SHIPPED.")
    @ApiResponses({
            @ApiResponse(responseCode = "403",
                    description = "Caller is not admin or manager",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404",
                    description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409",
                    description = "Order not in FULFILLING state",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

