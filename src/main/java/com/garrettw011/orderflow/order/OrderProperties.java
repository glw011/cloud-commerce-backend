package com.garrettw011.orderflow.order;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import java.math.BigDecimal;
import java.time.Duration;

@ConfigurationProperties(prefix = "app.order")
public record OrderProperties(
        @DefaultValue("0.08") BigDecimal taxRate,
        @DefaultValue("30m") Duration reservationTtl
) {}






