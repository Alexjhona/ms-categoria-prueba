package com.example.ms_categoria.exception;

import com.example.ms_categoria.config.RequestBodyCachingFilter;
import com.example.ms_categoria.dto.CategoriaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new HandlerTestController())
                .setControllerAdvice(new GlobalExceptionHandler(objectMapper))
                .addFilters(new RequestBodyCachingFilter())
                .build();
    }

    @Test
    @DisplayName("GlobalExceptionHandler - retorna formato estándar para validación 400")
    void handleValidation_RetornaFormatoEstandar() throws Exception {
        CategoriaDto dto = new CategoriaDto(null, "", "imagen.png");

        mockMvc.perform(post("/handler-test/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.mensaje", is("Se encontraron errores de validación")))
                .andExpect(jsonPath("$.ruta", is("/handler-test/validar")))
                .andExpect(jsonPath("$.datosRecibidos.nombre", is("")))
                .andExpect(jsonPath("$.datosRecibidos.imagen", is("imagen.png")))
                .andExpect(jsonPath("$.errores.nombre", is("Campo obligatorio")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - retorna formato estándar para 404")
    void handleNotFound_RetornaFormatoEstandar() throws Exception {
        mockMvc.perform(get("/handler-test/no-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.mensaje", is("No se encontró el recurso solicitado")))
                .andExpect(jsonPath("$.ruta", is("/handler-test/no-encontrado")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - retorna formato estándar para 409")
    void handleConflict_RetornaFormatoEstandar() throws Exception {
        mockMvc.perform(get("/handler-test/conflicto"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.mensaje", is("El registro ya existe o genera conflicto")))
                .andExpect(jsonPath("$.ruta", is("/handler-test/conflicto")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - retorna formato estándar para 500")
    void handleInternalError_RetornaFormatoEstandar() throws Exception {
        mockMvc.perform(get("/handler-test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.error", is("Internal Server Error")))
                .andExpect(jsonPath("$.mensaje", is("Ocurrió un error inesperado en el servidor")))
                .andExpect(jsonPath("$.ruta", is("/handler-test/error")));
    }

    @RestController
    @RequestMapping("/handler-test")
    private static class HandlerTestController {

        @PostMapping("/validar")
        CategoriaDto validar(@Valid @RequestBody CategoriaDto dto) {
            return dto;
        }

        @GetMapping("/no-encontrado")
        void noEncontrado() {
            throw new RecursoNoEncontradoException("No existe");
        }

        @GetMapping("/conflicto")
        void conflicto() {
            throw new ConflictoRecursoException("Duplicado");
        }

        @GetMapping("/error")
        void error() {
            throw new RuntimeException("Fallo inesperado");
        }
    }
}
