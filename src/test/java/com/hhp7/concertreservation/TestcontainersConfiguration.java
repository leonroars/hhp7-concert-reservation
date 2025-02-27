package com.hhp7.concertreservation;

import jakarta.annotation.PreDestroy;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
class TestcontainersConfiguration {

    public static final MySQLContainer<?> MYSQL_CONTAINER;
    public static final GenericContainer<?> REDIS_CONTAINER;
    public static final KafkaContainer KAFKA_CONTAINER;

    static {
        // 1) MySQL Container
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
                .withDatabaseName("mydatabase")
                .withUsername("myuser")
                .withPassword("secret");
        MYSQL_CONTAINER.start();

        // Apply MySQL connection properties
        System.setProperty("spring.datasource.url",
                MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
        System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

        // 2) Redis Container
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("bitnami/redis:7.4"))
                .withExposedPorts(6379)  // default redis port
                .withEnv("ALLOW_EMPTY_PASSWORD", "yes")
                .withEnv("REDIS_DISABLE_COMMANDS", "FLUSHDB,FLUSHALL");
        REDIS_CONTAINER.start();

        String redisHost = REDIS_CONTAINER.getHost();
        Integer redisPort = REDIS_CONTAINER.getFirstMappedPort();

        System.setProperty("spring.data.redis.host", redisHost);
        System.setProperty("spring.data.redis.port", redisPort.toString());

        // 3) Kafka Container
        KAFKA_CONTAINER = new KafkaContainer("apache/kafka-native:3.8.0");
        KAFKA_CONTAINER.start();
        System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());

        KAFKA_CONTAINER.start();
    }

    @PreDestroy
    public void preDestroy() {
        if (MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.stop();
        }
        if (REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.stop();
        }
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }
}