package com.garrettw011.orderflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @Schema(description = "opaque refresh token issued at login",
                example = "W8segqDDFp2aG0KMXd5U5W9x230rle4zOYusOSgALgg")
        @NotBlank String refreshToken
) {}
