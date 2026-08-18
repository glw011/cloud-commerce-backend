package com.garrettw011.orderflow.auth.dto;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresInSecs) {}
