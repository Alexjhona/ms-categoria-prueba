package com.example.ms_categoria.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CategoriaTest {

    @Test
    @DisplayName("Categoria - constructor vacío y setters")
    void categoria_ConstructorVacioYSetters() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Cargadores Móvil");

        assertEquals(1L, categoria.getId());
        assertEquals("Cargadores Móvil", categoria.getNombre());
    }

    @Test
    @DisplayName("Categoria - constructor con parámetros")
    void categoria_ConstructorConParametros() {
        Categoria categoria = new Categoria(2L, "Mouse Gaming");

        assertEquals(2L, categoria.getId());
        assertEquals("Mouse Gaming", categoria.getNombre());
    }

    @Test
    @DisplayName("Categoria - valores por defecto")
    void categoria_ValoresPorDefecto() {
        Categoria categoria = new Categoria();

        assertNull(categoria.getId());
        assertNull(categoria.getNombre());
    }
}