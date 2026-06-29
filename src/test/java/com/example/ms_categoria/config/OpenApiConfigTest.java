package com.example.ms_categoria.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenApiConfigTest {

    @Test
    @DisplayName("OpenApiConfig - crea metadatos de documentación")
    void customOpenAPI_CreaMetadatosDeDocumentacion() {
        OpenAPI openAPI = new OpenApiConfig().customOpenAPI();

        assertNotNull(openAPI);
        assertEquals("OPEN API MICROSERVICIO CATEGORIA", openAPI.getInfo().getTitle());
        assertEquals("0.0.1", openAPI.getInfo().getVersion());
        assertEquals(
                "Documentacion de endpoints para registrar, consultar, actualizar y eliminar categorias usadas para clasificar productos.",
                openAPI.getInfo().getDescription());
        assertEquals("http://swagger.io/terms", openAPI.getInfo().getTermsOfService());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
        assertEquals("http://springdoc.org", openAPI.getInfo().getLicense().getUrl());
    }
}
