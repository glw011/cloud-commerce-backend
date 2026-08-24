package com.garrettw011.orderflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = DataRedisRepositoriesAutoConfiguration.class)
public class OrderflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderflowApplication.class, args);
	}

}
