package com.garrettw011.orderflow.auth;

import com.garrettw011.orderflow.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JwtFilterTest extends AbstractIntegrationTest {
    @Autowired
    JwtProperties jwtProperties;

    @Test
    void rejectsExpiredTokens() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder()
                .subject("1")
                .issuedAt(Date.from(Instant.now().minusSeconds(7200)))
                .expiration(Date.from(Instant.now().minusSeconds(3600)))
                .signWith(key).compact();

        mvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(expired)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsBadSignatureTokens() throws Exception {
        SecretKey badKey = Keys.hmacShaKeyFor("Ahoy-matey!-Here-be-a-BAD-secret-key".getBytes(StandardCharsets.UTF_8));

        String forgery = Jwts.builder()
                .subject("1")
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(badKey).compact();

        mvc.perform(get("/api/v1/auth/me").header("Authorization", bearer(forgery)))
                .andExpect(status().isUnauthorized());
    }
}
