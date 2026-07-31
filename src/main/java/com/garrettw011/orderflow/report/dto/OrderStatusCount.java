package com.garrettw011.orderflow.report.dto;

import com.garrettw011.orderflow.order.OrderStatus;

public record OrderStatusCount(OrderStatus status, Long count) {}

