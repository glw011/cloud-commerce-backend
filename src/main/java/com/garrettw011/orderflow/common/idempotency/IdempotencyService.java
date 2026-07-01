package com.garrettw011.orderflow.common.idempotency;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Optional;

@Service
public class IdempotencyService {
    public static final String PROCESSING = "__PROCESSING__";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redis;

    public IdempotencyService(StringRedisTemplate redis) { this.redis = redis; }

    private String redisKey(String scope, String key) {
        return "idem:" + scope + ":" + key;
    }

    // stores value for this key (if exists)
    public Optional<String> find(String scope, String key) {
        return Optional.ofNullable(redis.opsForValue().get(redisKey(scope, key)));
    }

    /** Claims key for processing
     *    Returns:
     *      TRUE  -> if won claim
     *      FALSE -> if already exist
     */
    public boolean claim(String scope, String key) {
        Boolean won = redis.opsForValue().setIfAbsent(redisKey(scope, key), PROCESSING, TTL);
        return Boolean.TRUE.equals(won);
    }

    // records result + replaces 'PROCESSING' marker
    public void store(String scope, String key, String value) {
        redis.opsForValue().set(redisKey(scope, key), value, TTL);
    }

    // release claim after failure
    public void release(String scope, String key) { redis.delete(redisKey(scope, key)); }

    // stores result of 1st op to use for potential retries
    public void remember(String scope, String key, String value) {
        redis.opsForValue().set(redisKey(scope, key), value, TTL);
    }
}







