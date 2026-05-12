package com.example.ms_categoria.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CategoriaDtoTest {

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
}