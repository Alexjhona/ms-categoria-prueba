package com.example.ms_categoria.integration;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas de integracion - CategoriaController")
class CategoriaControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
    }

    @Order(1)
    @Test
    @DisplayName("POST /api/categorias - crea categoria y retorna HTTP 200")
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
    @DisplayName("POST /api/categorias - rechaza nombre vacio")
    void crearCategoria_NombreVacio_RetornaBadRequest() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje", is("Se encontraron errores de validación")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias")))
                .andExpect(jsonPath("$.datosRecibidos.nombre", is("")))
                .andExpect(jsonPath("$.errores.nombre", is("Campo obligatorio")));
    }

    @Order(3)
    @Test
    @DisplayName("POST /api/categorias - retorna multiples errores de campos reales")
    void crearCategoria_MultiplesCamposInvalidos_RetornaBadRequest() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "", "x".repeat(1001));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje", is("Se encontraron errores de validación")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias")))
                .andExpect(jsonPath("$.datosRecibidos.nombre", is("")))
                .andExpect(jsonPath("$.datosRecibidos.imagen", is("x".repeat(1001))))
                .andExpect(jsonPath("$.errores.nombre", is("Campo obligatorio")))
                .andExpect(jsonPath("$.errores.imagen", is("No debe superar 1000 caracteres")));
    }

    @Order(4)
    @Test
    @DisplayName("GET /api/categorias - retorna lista de categorias")
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

    @Order(5)
    @Test
    @DisplayName("GET /api/categorias/{id} - retorna categoria existente")
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

    @Order(6)
    @Test
    @DisplayName("PUT /api/categorias/{id} - actualiza categoria")
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

    @Order(7)
    @Test
    @DisplayName("PUT /api/categorias/{id} - rechaza nombre nulo")
    void actualizarCategoria_NombreNulo_RetornaBadRequest() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Nombre Inicial");

        String respuesta = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CategoriaDto creada = objectMapper.readValue(respuesta, CategoriaDto.class);
        CategoriaDto dtoActualizado = new CategoriaDto(null, null);

        mockMvc.perform(put("/api/categorias/{id}", creada.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoActualizado)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje", is("Se encontraron errores de validación")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias/" + creada.getId())))
                .andExpect(jsonPath("$.errores.nombre", is("Campo obligatorio")));
    }

    @Order(8)
    @Test
    @DisplayName("POST /api/categorias - rechaza tipo de dato invalido")
    void crearCategoria_TipoDatoInvalido_RetornaBadRequest() throws Exception {
        String body = """
                {
                  "id": "abc",
                  "nombre": "Cables USB",
                  "imagen": "imagen.png"
                }
                """;

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje", is("Se encontraron errores de validación")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias")))
                .andExpect(jsonPath("$.datosRecibidos.id", is("abc")))
                .andExpect(jsonPath("$.datosRecibidos.nombre", is("Cables USB")))
                .andExpect(jsonPath("$.errores.id", is("Tipo de dato inválido o estructura incorrecta")));
    }

    @Order(9)
    @Test
    @DisplayName("GET /api/categorias/{id} - retorna 404 uniforme")
    void obtenerCategoria_NoExiste_RetornaNotFound() throws Exception {
        mockMvc.perform(get("/api/categorias/{id}", 99999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje", is("No se encontró el recurso solicitado")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias/99999")));
    }

    @Order(10)
    @Test
    @DisplayName("POST /api/categorias - retorna 409 uniforme por duplicado")
    void crearCategoria_Duplicada_RetornaConflict() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Categoria Duplicada");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje", is("El registro ya existe o genera conflicto")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias")));
    }

    @Order(11)
    @Test
    @DisplayName("DELETE /api/categorias/{id} - elimina categoria")
    void eliminarCategoria_RetornaNoContent() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "Categoria Temporal");

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

    @Order(12)
    @Test
    @DisplayName("DELETE /api/categorias/{id} - retorna 404 uniforme")
    void eliminarCategoria_NoExiste_RetornaNotFound() throws Exception {
        mockMvc.perform(delete("/api/categorias/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje", is("No se encontró el recurso solicitado")))
                .andExpect(jsonPath("$.ruta", is("/api/categorias/99999")));
    }
}
