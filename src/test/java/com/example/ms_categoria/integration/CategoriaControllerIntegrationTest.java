package com.example.ms_categoria.integration;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas de integración - CategoriaController")
class CategoriaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
    }

    @Order(1)
    @Test
    @DisplayName("POST /api/categorias - crea categoría y retorna HTTP 200")
    void crearCategoria_RetornaOk() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Cables HDMI");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre", is("Cables HDMI")));
    }

    @Order(2)
    @Test
    @DisplayName("GET /api/categorias - retorna lista de categorías")
    void listarCategorias_RetornaLista() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Adaptadores USB");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/categorias")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nombre", is("Adaptadores USB")));
    }

    @Order(3)
    @Test
    @DisplayName("GET /api/categorias/{id} - retorna categoría existente")
    void obtenerCategoria_RetornaCategoria() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Mouse Gaming");

        String respuesta = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoriaDto creada = objectMapper.readValue(respuesta, CategoriaDto.class);

        mockMvc.perform(get("/api/categorias/{id}", creada.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(creada.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Mouse Gaming")));
    }

    @Order(4)
    @Test
    @DisplayName("PUT /api/categorias/{id} - actualiza categoría")
    void actualizarCategoria_RetornaCategoriaActualizada() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Nombre Inicial");

        String respuesta = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoriaDto creada = objectMapper.readValue(respuesta, CategoriaDto.class);

        CategoriaDto dtoActualizado = new CategoriaDto(null, "Nombre Actualizado");

        mockMvc.perform(put("/api/categorias/{id}", creada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(creada.getId().intValue())))
                .andExpect(jsonPath("$.nombre", is("Nombre Actualizado")));
    }

    @Order(5)
    @Test
    @DisplayName("DELETE /api/categorias/{id} - elimina categoría")
    void eliminarCategoria_RetornaNoContent() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Categoría Temporal");

        String respuesta = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoriaDto creada = objectMapper.readValue(respuesta, CategoriaDto.class);

        mockMvc.perform(delete("/api/categorias/{id}", creada.getId()))
                .andExpect(status().isNoContent());
    }
}