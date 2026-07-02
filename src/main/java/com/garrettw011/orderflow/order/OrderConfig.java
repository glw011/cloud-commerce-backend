package com.garrettw011.orderflow.order;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OrderProperties.class)
public class OrderConfig {}

