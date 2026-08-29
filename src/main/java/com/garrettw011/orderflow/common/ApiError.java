package com.garrettw011.orderflow.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;
import java.time.Instant;
import java.util.List;

@Schema(description = "Standard API error response")
public record ApiError(
        @Schema(description = "when the error occurred", example = "2026-07-31T14:30:00Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "409")
        int status,

        @Schema(description = "status phrase", example = "Conflict")
        String error,

        @Schema(description = "error details (nullable)", example = "Insufficient stock for 'KM-KB-MECH-001' (available 3, requested 4).")
        @Nullable String message,

        @Schema(description = "requested path that produced error", example = "/api/v1/orders")
        String path,

        @Schema(description = "id of request that produced error (nullable)",
                example = "4efc8496-1fbf-430e-9229-59f31118d335")
        @Nullable String requestId,

        @Schema(description = "validation errors per field for validation failures (nullable)")
        @Nullable List<FieldError> fieldErrors
) {
    public record FieldError(
            @Schema(description = "field that failed validation", example = "price")
            String field,

            @Schema(description = "reason for failure", example = "must be greater than 0")
            String message
    ) {}
}

