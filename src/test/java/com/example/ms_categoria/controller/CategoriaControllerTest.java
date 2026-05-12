package com.example.ms_categoria.controller;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.service.CategoriaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoriaControllerTest {

    private MockMvc mockMvc;
    private CategoriaService categoriaService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        categoriaService = Mockito.mock(CategoriaService.class);
        CategoriaController categoriaController = new CategoriaController(categoriaService);
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/categorias - crear categoría")
    void crearCategoria_DebeRetornarOk() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "Cables USB");
        CategoriaDto salida = new CategoriaDto(1L, "Cables USB");

        when(categoriaService.crearCategoria(Mockito.any(CategoriaDto.class))).thenReturn(salida);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Cables USB"));

        verify(categoriaService).crearCategoria(Mockito.any(CategoriaDto.class));
    }

    @Test
    @DisplayName("GET /api/categorias/{id} - obtener categoría")
    void obtenerCategoria_DebeRetornarOk() throws Exception {
        CategoriaDto salida = new CategoriaDto(1L, "Mouse Gaming");

        when(categoriaService.obtenerCategoria(1L)).thenReturn(salida);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Mouse Gaming"));

        verify(categoriaService).obtenerCategoria(1L);
    }

    @Test
    @DisplayName("GET /api/categorias - listar categorías")
    void listarCategorias_DebeRetornarOk() throws Exception {
        CategoriaDto cat1 = new CategoriaDto(1L, "Cables USB");
        CategoriaDto cat2 = new CategoriaDto(2L, "Cargadores Móvil");

        when(categoriaService.listarCategorias()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Cables USB"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nombre").value("Cargadores Móvil"));

        verify(categoriaService).listarCategorias();
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} - actualizar categoría")
    void actualizarCategoria_DebeRetornarOk() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "Nombre Nuevo");
        CategoriaDto salida = new CategoriaDto(1L, "Nombre Nuevo");

        when(categoriaService.actualizarCategoria(Mockito.eq(1L), Mockito.any(CategoriaDto.class)))
                .thenReturn(salida);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Nombre Nuevo"));

        verify(categoriaService).actualizarCategoria(Mockito.eq(1L), Mockito.any(CategoriaDto.class));
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - eliminar categoría")
    void eliminarCategoria_DebeRetornarNoContent() throws Exception {
        doNothing().when(categoriaService).eliminarCategoria(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).eliminarCategoria(1L);
    }
}