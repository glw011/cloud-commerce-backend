package com.garrettw011.orderflow;

import org.springframework.boot.SpringApplication;

public class TestOrderflowApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderflowApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
