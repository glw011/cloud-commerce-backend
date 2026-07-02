package com.garrettw011.orderflow.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineRequest(
        @NotNull Long productId,
        @Positive int quantity
) {}


