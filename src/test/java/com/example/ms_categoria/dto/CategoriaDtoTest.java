package com.example.ms_categoria.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CategoriaDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CategoriaDto - constructor vacío y setters")
    void categoriaDto_ConstructorVacioYSetters() {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(1L);
        dto.setNombre("Cables USB");

        assertEquals(1L, dto.getId());
        assertEquals("Cables USB", dto.getNombre());
    }

    @Test
    @DisplayName("CategoriaDto - constructor con parámetros")
    void categoriaDto_ConstructorConParametros() {
        CategoriaDto dto = new CategoriaDto(2L, "Mouse Gaming");

        assertEquals(2L, dto.getId());
        assertEquals("Mouse Gaming", dto.getNombre());
    }

    @Test
    @DisplayName("CategoriaDto - valores por defecto")
    void categoriaDto_ValoresPorDefecto() {
        CategoriaDto dto = new CategoriaDto();

        assertNull(dto.getId());
        assertNull(dto.getNombre());
    }

    @Test
    @DisplayName("CategoriaDto - valida campos correctos")
    void categoriaDto_Valido_NoTieneViolaciones() {
        CategoriaDto dto = new CategoriaDto(1L, "Cables USB", "imagen.png");

        Set<ConstraintViolation<CategoriaDto>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CategoriaDto - rechaza nombre obligatorio")
    void categoriaDto_NombreVacio_TieneViolacion() {
        CategoriaDto dto = new CategoriaDto(null, "", "imagen.png");

        Set<ConstraintViolation<CategoriaDto>> violations = validator.validate(dto);

        assertThat(violations)
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("nombre");
                    assertThat(violation.getMessage()).isEqualTo("Campo obligatorio");
                });
    }

    @Test
    @DisplayName("CategoriaDto - rechaza tamaños máximos")
    void categoriaDto_TamaniosMaximos_TieneViolaciones() {
        CategoriaDto dto = new CategoriaDto(null, "x".repeat(121), "x".repeat(1001));

        Set<ConstraintViolation<CategoriaDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("nombre", "imagen");
    }
}
