package com.example.ms_categoria;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class MsCategoriaApplicationTests {

    @Test
    void applicationClassExists() {
        MsCategoriaApplication application = new MsCategoriaApplication();
        assertNotNull(application);
    }

    @Test
    void mainRunsSpringApplication() {
        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            String[] args = {"--spring.profiles.active=test"};

            MsCategoriaApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(MsCategoriaApplication.class, args));
        }
    }
}
