package com.garrettw011.orderflow.common;

import org.jspecify.annotations.Nullable;
import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        @Nullable String message,
        String path,
        @Nullable String requestId,
        @Nullable List<FieldError> fieldErrors
) { public record FieldError(String field, String message) {} }

