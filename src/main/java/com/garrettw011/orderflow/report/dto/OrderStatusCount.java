package com.garrettw011.orderflow.report.dto;

import com.garrettw011.orderflow.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record OrderStatusCount(
        @Schema(description = "order status", example = "PAID")
        OrderStatus status,

        @Schema(description = "number of orders in this status", example = "17")
        Long count
) {}

