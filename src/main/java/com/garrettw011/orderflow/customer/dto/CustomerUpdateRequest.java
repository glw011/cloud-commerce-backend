package com.garrettw011.orderflow.customer.dto;

import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record CustomerUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Nullable String phone
) {}
