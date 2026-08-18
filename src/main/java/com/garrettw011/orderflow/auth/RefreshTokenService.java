package com.garrettw011.orderflow.auth;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private static final String KEY_PREFIX = "refresh:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redis;
    private final Duration ttl;

    public RefreshTokenService(StringRedisTemplate redis, JwtProperties props) {
        this.redis = redis;
        this.ttl = props.refreshTokenTtl();
    }

    private String newToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // issue new refresh token bound to user
    public String issue(Long userId) {
        String token = newToken();
        redis.opsForValue().set(KEY_PREFIX + token, String.valueOf(userId), ttl);
        return token;
    }

    // validate + rotate (returns userId & deletes token to ensure single-use)
    public Long consume(String token) {
        String key = KEY_PREFIX + token;
        String userId = redis.opsForValue().get(key);

        if (userId == null) { throw new BadCredentialsException("Invalid or expired refresh token."); }
        redis.delete(key);
        return Long.valueOf(userId);
    }

    public void revoke(String token) { redis.delete(KEY_PREFIX + token); }
}

