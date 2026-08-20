package com.garrettw011.orderflow.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.jspecify.annotations.Nullable;

public record CustomerUpdateRequest(
        @Schema(example = "Isaac")
        @NotBlank String firstName,

        @Schema(example = "Asimov")
        @NotBlank String lastName,

        @Schema(description = "optional customer contact number (nullable)", example = "+1-234-567-8910")
        @Nullable String phone
) {}
