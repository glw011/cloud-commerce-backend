package com.garrettw011.orderflow.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateRequest(
        @NotEmpty List<@Valid OrderLineRequest> items
) {}


