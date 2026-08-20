package com.garrettw011.orderflow.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateRequest(
        @Schema(description = "1+ number of order items (duplicate productId quantities are merged)")
        @NotEmpty List<@Valid OrderLineRequest> items
) {}


