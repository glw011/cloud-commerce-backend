package com.garrettw011.orderflow.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineRequest(
        @Schema(description = "Id of product to order", example = "3")
        @NotNull Long productId,

        @Schema(description = "Quantity to order", example = "2", minimum = "1")
        @Positive int quantity
) {}


