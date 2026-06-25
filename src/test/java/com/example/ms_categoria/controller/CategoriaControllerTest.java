package com.example.ms_categoria.controller;

import com.example.ms_categoria.config.RequestBodyCachingFilter;
import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.exception.ConflictoRecursoException;
import com.example.ms_categoria.exception.GlobalExceptionHandler;
import com.example.ms_categoria.exception.RecursoNoEncontradoException;
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

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setControllerAdvice(new GlobalExceptionHandler(objectMapper))
                .addFilters(new RequestBodyCachingFilter())
                .build();
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
    @DisplayName("POST /api/categorias - rechaza nombre vacio")
    void crearCategoria_NombreVacio_DebeRetornarBadRequest() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias"))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value(""))
                .andExpect(jsonPath("$.errores.nombre").value("Campo obligatorio"));

        Mockito.verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("POST /api/categorias - retorna multiples errores de campos reales")
    void crearCategoria_MultiplesCamposInvalidos_DebeRetornarBadRequest() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "", "x".repeat(1001));

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias"))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value(""))
                .andExpect(jsonPath("$.datosRecibidos.imagen").value("x".repeat(1001)))
                .andExpect(jsonPath("$.errores.nombre").value("Campo obligatorio"))
                .andExpect(jsonPath("$.errores.imagen").value("No debe superar 1000 caracteres"));

        Mockito.verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("POST /api/categorias - rechaza tipo de dato invalido")
    void crearCategoria_TipoDatoInvalido_DebeRetornarBadRequest() throws Exception {
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
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias"))
                .andExpect(jsonPath("$.datosRecibidos.id").value("abc"))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value("Cables USB"))
                .andExpect(jsonPath("$.datosRecibidos.imagen").value("imagen.png"))
                .andExpect(jsonPath("$.errores.id").value("Tipo de dato inválido o estructura incorrecta"));

        Mockito.verifyNoInteractions(categoriaService);
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
    @DisplayName("GET /api/categorias/{id} - retorna 404 uniforme")
    void obtenerCategoria_NoExiste_DebeRetornarNotFound() throws Exception {
        when(categoriaService.obtenerCategoria(99L))
                .thenThrow(new RecursoNoEncontradoException("Categoría no encontrada con id: 99"));

        mockMvc.perform(get("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró el recurso solicitado"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias/99"));

        verify(categoriaService).obtenerCategoria(99L);
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
    @DisplayName("PUT /api/categorias/{id} - rechaza nombre nulo")
    void actualizarCategoria_NombreNulo_DebeRetornarBadRequest() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, null);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias/1"))
                .andExpect(jsonPath("$.datosRecibidos.nombre").value(nullValue()))
                .andExpect(jsonPath("$.errores.nombre").value("Campo obligatorio"));

        Mockito.verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} - retorna 409 uniforme")
    void actualizarCategoria_Conflicto_DebeRetornarConflict() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "Nombre Existente", "imagen.png");

        when(categoriaService.actualizarCategoria(Mockito.eq(1L), Mockito.any(CategoriaDto.class)))
                .thenThrow(new ConflictoRecursoException("Ya existe otro registro con ese nombre"));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.mensaje").value("El registro ya existe o genera conflicto"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias/1"));

        verify(categoriaService).actualizarCategoria(Mockito.eq(1L), Mockito.any(CategoriaDto.class));
    }

    @Test
    @DisplayName("PUT /api/categorias/{id} - retorna 400 por parametro invalido")
    void actualizarCategoria_IdInvalido_DebeRetornarBadRequest() throws Exception {
        CategoriaDto entrada = new CategoriaDto(null, "Nombre Nuevo", "imagen.png");

        mockMvc.perform(put("/api/categorias/abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Se encontraron errores de validación"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias/abc"))
                .andExpect(jsonPath("$.datosRecibidos").isMap())
                .andExpect(jsonPath("$.errores.id").value("Tipo de dato inválido"));

        Mockito.verifyNoInteractions(categoriaService);
    }

    @Test
    @DisplayName("GET /api/categorias - retorna 500 uniforme")
    void listarCategorias_ErrorInesperado_DebeRetornarInternalServerError() throws Exception {
        when(categoriaService.listarCategorias()).thenThrow(new RuntimeException("Fallo no esperado"));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error inesperado en el servidor"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias"));

        verify(categoriaService).listarCategorias();
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - eliminar categoría")
    void eliminarCategoria_DebeRetornarNoContent() throws Exception {
        doNothing().when(categoriaService).eliminarCategoria(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService).eliminarCategoria(1L);
    }

    @Test
    @DisplayName("DELETE /api/categorias/{id} - retorna 404 uniforme")
    void eliminarCategoria_NoExiste_DebeRetornarNotFound() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe categoría con id: 99"))
                .when(categoriaService).eliminarCategoria(99L);

        mockMvc.perform(delete("/api/categorias/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("No se encontró el recurso solicitado"))
                .andExpect(jsonPath("$.ruta").value("/api/categorias/99"));

        verify(categoriaService).eliminarCategoria(99L);
    }
}
