package com.garrettw011.orderflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record RegisterRequest(
        @Schema(description = "account email", example = "customer@example.com")
        @NotBlank @Email String email,

        @Schema(description = "account password", example = "CustomerPass123!", minLength = 8)
        @NotBlank @Size(min=8, max=100) String password,

        @Schema(example = "John")
        @NotBlank String firstName,

        @Schema(example = "Searle")
        @NotBlank String lastName,

        @Schema(description = "optional contact number (nullable)", example = "+1-234-567-8910")
        @Nullable String phone
) {}
