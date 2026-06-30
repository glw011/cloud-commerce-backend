package com.garrettw011.orderflow.customer.dto;

import org.jspecify.annotations.Nullable;
import java.time.Instant;

public record CustomerResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        @Nullable String phone,
        Instant createdAt,
        Instant updatedAt
) {}
