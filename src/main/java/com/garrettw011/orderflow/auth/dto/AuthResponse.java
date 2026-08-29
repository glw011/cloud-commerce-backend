package com.garrettw011.orderflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "JWT bearer token",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicm9sZSI6IkNVU1RPTUVSIn0.TBvcoMRN2-0ai2RcfnzY9m_Rrcjq")
        String accessToken,

        @Schema(description = "opaque refresh token (single use)",
                example = "W8segqDDFp2aG0KMXd5U5W9x230rle4zOYusOSgALgg")
        String refreshToken,

        @Schema(description = "auth scheme for access token", example = "Bearer")
        String tokenType,

        @Schema(description = "lifetime of access token", example = "900")
        long expiresInSecs
) {}
