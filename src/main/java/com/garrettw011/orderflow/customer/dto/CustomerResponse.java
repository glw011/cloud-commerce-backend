package com.garrettw011.orderflow.customer.dto;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phone,
        Instant createdAt,
        Instant updatedAt
) {}
