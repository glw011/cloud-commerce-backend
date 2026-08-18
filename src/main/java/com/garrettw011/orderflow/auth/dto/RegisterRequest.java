package com.garrettw011.orderflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min=8, max=100) String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Nullable String phone
) {}

