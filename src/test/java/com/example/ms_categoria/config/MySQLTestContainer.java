package com.example.ms_categoria.config;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class MySQLTestContainer {

    public static final MySQLContainer<?> INSTANCE;

    static {
        INSTANCE = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("chinito_categoria_test")
                .withUsername("test")
                .withPassword("test");

        INSTANCE.start();
    }

    private MySQLTestContainer() {
    }
}