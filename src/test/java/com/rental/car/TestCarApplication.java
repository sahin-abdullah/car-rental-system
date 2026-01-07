package com.rental.car;

import org.springframework.boot.SpringApplication;

public class TestCarApplication {

	public static void main(String[] args) {
		SpringApplication.from(CarRentalApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
