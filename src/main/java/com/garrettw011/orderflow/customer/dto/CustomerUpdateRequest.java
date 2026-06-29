package com.garrettw011.orderflow.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String phone
) {}
