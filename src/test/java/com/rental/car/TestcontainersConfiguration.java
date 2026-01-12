package com.rental.car;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

	@Bean(initMethod = "start", destroyMethod = "stop")
	@ServiceConnection
	@SuppressWarnings("resource")
	PostgreSQLContainer<?> postgresContainer() {
		DockerImageName image = DockerImageName.parse("postgis/postgis:16-3.4")
				.asCompatibleSubstituteFor("postgres");
		return new PostgreSQLContainer<>(image)
				.withDatabaseName("rentaldb")
				.withUsername("rental_user")
				.withPassword("rental_pass");
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	@ServiceConnection(name = "redis")
	@SuppressWarnings("resource")
	GenericContainer<?> redisContainer() {
		return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
				.withExposedPorts(6379);
	}

}
