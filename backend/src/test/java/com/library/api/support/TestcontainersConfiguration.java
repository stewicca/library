package com.library.api.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Spins up real PostgreSQL + Redis containers for integration tests.
 *
 * <p>{@code @ServiceConnection} auto-wires each container's connection details into Spring Boot,
 * so no manual {@code spring.datasource.*}/{@code spring.data.redis.*} property juggling is needed.
 * Import this into any {@code @SpringBootTest} that needs the database or Redis.</p>
 *
 * <p>Requires a running Docker daemon.</p>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));
    }

    @Bean
    @ServiceConnection(name = "redis")
    GenericContainer<?> redisContainer() {
        return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                .withExposedPorts(6379);
    }
}
