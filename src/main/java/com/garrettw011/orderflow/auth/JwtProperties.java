package com.garrettw011.orderflow.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Duration accessTokenTtl,
        Duration refreshTokenTtl
) {
    private static final int SECRET_MIN_BYTES = 32;

    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET environment variable is unset or empty.");
        }

        int bytes = secret.getBytes(StandardCharsets.UTF_8).length;
        if (bytes < SECRET_MIN_BYTES) {
            throw new IllegalStateException("JWT_SECRET must be at least " + SECRET_MIN_BYTES +
                    " Bytes (current val is " + bytes + " bytes).");
        }
    }
}
