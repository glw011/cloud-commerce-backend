package com.garrettw011.orderflow.support;

import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Owns containers shared by entire test suite so only 1 Postgres/Redis starts per JVM and
 * are reused for each test class
 *
 * `ENABLE_TESTCONTAINERS` (env var): true | false
 *   true:
 *     - Start Testcontainers for Postgres/Redis used by test suite
 *   false:
 *     - Skip Testcontainers & connect to Compose services via `spring.datasource.*`/`spring.data.redis.*`
 *       vals from `application-test.yml`
 */
public abstract class AbstractContainerTest {
    private static final boolean TESTCONTAINERS_ENABLED = resolveTestcontainersEnabled();

    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(
            DockerImageName.parse("postgres:16-alpine"));
    static final GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

    static {
        if (TESTCONTAINERS_ENABLED) {
            POSTGRES.start();
            REDIS.start();
        }
    }

    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        if (!TESTCONTAINERS_ENABLED) {
            return;  // use vals in `application-test.yml`
        }

        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    private static boolean resolveTestcontainersEnabled() {
        String raw = System.getenv("ENABLE_TESTCONTAINERS");
        // unset -> default true
        if (raw == null || raw.isBlank()) { return true; }

        String val = raw.trim().toLowerCase();
        return switch (val) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new IllegalStateException("Invalid value: ENABLE_TESTCONTAINERS=\"" + raw +
                    "\"\nExpected: \"true\" | \"false\"");
        };
    }
}
