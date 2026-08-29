package com.garrettw011.orderflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CurrentUserResponse(
        @Schema(description = "user id", example = "2")
        Long id,

        @Schema(example = "customer@example.com")
        String email,

        @Schema(example = "CUSTOMER")
        String role
) {}
