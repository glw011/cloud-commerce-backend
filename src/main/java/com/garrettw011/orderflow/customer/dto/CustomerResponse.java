package com.garrettw011.orderflow.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;
import java.time.Instant;

public record CustomerResponse(
        @Schema(description = "customer id", example = "2")
        Long id,

        @Schema(description = "account email", example = "customer@example.com")
        String email,

        @Schema(example = "Geoffrey")
        String firstName,

        @Schema(example = "Hinton")
        String lastName,

        @Schema(description = "optional customer contact number (nullable)", example = "+1-234-567-8910")
        @Nullable String phone,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant createdAt,

        @Schema(example = "2026-07-04T00:33:06Z")
        Instant updatedAt
) {}
