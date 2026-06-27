package com.garrettw011.orderflow.common.idempotency;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;

@Service
public class IdempotencyService {
    private static final Duration TTL = Duration.ofHours(24);
    private final StringRedisTemplate redis;

    public IdempotencyService(StringRedisTemplate redis) { this.redis = redis; }

    private String redisKey(String scope, String key) {
        return "idem:" + scope + ":" + key;
    }

    // result stored for key on previous call (if exists)
    public Optional<String> find(String scope, String key) {
        return Optional.ofNullable(redis.opsForValue().get(redisKey(scope, key)));
    }

    // stores result of 1st op to use for potential retries
    public void remember(String scope, String key, String value) {
        redis.opsForValue().set(redisKey(scope, key), value, TTL);
    }
}







