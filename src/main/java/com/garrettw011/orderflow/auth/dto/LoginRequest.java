package com.garrettw011.orderflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "customer@example.com")
        @NotBlank @Email String email,

        @Schema(example = "CustomerPass123!")
        @NotBlank String password
) {}
