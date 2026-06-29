package com.example.ms_categoria.exception;

import com.example.ms_categoria.config.RequestBodyCachingFilter;
import com.example.ms_categoria.dto.CategoriaDto;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    @Test
    @DisplayName("GlobalExceptionHandler - maneja JSON inválido")
    void handleInvalidBody_RetornaErroresDeJson() throws Exception {
        mockMvc.perform(post("/handler-test/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.datosRecibidos", is("{\"nombre\":")))
                .andExpect(jsonPath("$.errores.request", is("El cuerpo de la solicitud no tiene un formato JSON válido")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja paths de JSON mapping")
    void handleInvalidBody_RetornaCampoDesdeJsonMapping() {
        JsonMappingException mappingException =
                JsonMappingException.from((com.fasterxml.jackson.databind.DeserializationContext) null, "Tipo inválido");
        mappingException.prependPath(new Object(), 0);
        mappingException.prependPath(new Object(), "categorias");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Error", mappingException, null);

        Map<String, Object> body = handler().handleInvalidBody(exception, request()).getBody();

        assertEquals(Map.of("categorias.[0]", "Tipo de dato inválido o estructura incorrecta"), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja violaciones de constraints")
    void handleConstraintViolation_RetornaErroresPorCampo() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("crear.arg0.nombre");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("");

        Map<String, Object> body = handler()
                .handleConstraintViolation(new jakarta.validation.ConstraintViolationException(Set.of(violation)), request())
                .getBody();

        assertEquals(Map.of("nombre", "Valor inválido"), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja constraint sin ruta anidada")
    void handleConstraintViolation_RetornaCampoSinRutaAnidada() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("nombre");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("Campo obligatorio");

        Map<String, Object> body = handler()
                .handleConstraintViolation(new jakarta.validation.ConstraintViolationException(Set.of(violation)), request())
                .getBody();

        assertEquals(Map.of("nombre", "Campo obligatorio"), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - convierte fallback de validación")
    void handleValidation_ConvierteFallbackSiNoHayBody() throws Exception {
        CategoriaDto dto = new CategoriaDto(1L, "Audio", "audio.png");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(dto, "categoriaDto");
        bindingResult.addError(new ObjectError("categoriaDto", ""));
        Method method = HandlerTestController.class.getDeclaredMethod("validar", CategoriaDto.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        Map<String, Object> body = handler()
                .handleValidation(new org.springframework.web.bind.MethodArgumentNotValidException(parameter, bindingResult), request())
                .getBody();

        assertEquals(Map.of("categoriaDto", "Valor inválido"), body.get("errores"));
        assertEquals(Map.of("id", 1L, "nombre", "Audio", "imagen", "audio.png"), body.get("datosRecibidos"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja parámetro faltante")
    void handleInvalidParameter_RetornaCampoObligatorio() throws Exception {
        mockMvc.perform(get("/handler-test/requiere-parametro"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre", is("Campo obligatorio")));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja tipo de parámetro inválido")
    void handleInvalidParameter_RetornaTipoInvalido() throws Exception {
        Method method = HandlerTestController.class.getDeclaredMethod("porId", Long.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException("abc", Long.class, "id", parameter, new NumberFormatException());

        Map<String, Object> body = handler().handleInvalidParameter(exception, request()).getBody();

        assertEquals(Map.of("id", "Tipo de dato inválido"), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - tolera parámetro inválido sin subtipo conocido")
    void handleInvalidParameter_RetornaErroresVaciosParaSubtipoNoConocido() {
        Map<String, Object> body = handler().handleInvalidParameter(new Exception("Parámetro inválido"), request()).getBody();

        assertEquals(Map.of(), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja IllegalArgumentException como conflicto")
    void handleIllegalArgument_RetornaConflictSiMensajeEsDuplicado() {
        var response = handler().handleIllegalArgument(new IllegalArgumentException("valor duplicado"), request());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("El registro ya existe o genera conflicto", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - detecta variantes de conflicto")
    void handleIllegalArgument_DetectaVariantesDeConflicto() {
        assertEquals(HttpStatus.CONFLICT,
                handler().handleIllegalArgument(new IllegalArgumentException("ya existe"), request()).getStatusCode());
        assertEquals(HttpStatus.CONFLICT,
                handler().handleIllegalArgument(new IllegalArgumentException("unique constraint"), request()).getStatusCode());
        assertEquals(HttpStatus.CONFLICT,
                handler().handleIllegalArgument(new IllegalArgumentException("conflict detected"), request()).getStatusCode());
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja IllegalArgumentException como bad request")
    void handleIllegalArgument_RetornaBadRequestSiNoEsConflicto() {
        var response = handler().handleIllegalArgument(new IllegalArgumentException("valor inválido"), request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("valor inválido", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - usa mensaje por defecto si IllegalArgumentException es nulo")
    void handleIllegalArgument_RetornaBadRequestSiMensajeEsNulo() {
        var response = handler().handleIllegalArgument(new IllegalArgumentException((String) null), request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Valor inválido", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja ResponseStatusException")
    void handleResponseStatus_RetornaMensajePorStatus() {
        assertEquals("No se encontró el recurso solicitado",
                handler().handleResponseStatus(new ResponseStatusException(HttpStatus.NOT_FOUND, "x"), request())
                        .getBody().get("mensaje"));
        assertEquals("El registro ya existe o genera conflicto",
                handler().handleResponseStatus(new ResponseStatusException(HttpStatus.CONFLICT, "x"), request())
                        .getBody().get("mensaje"));
        assertEquals("Ocurrió un error inesperado en el servidor",
                handler().handleResponseStatus(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "x"), request())
                        .getBody().get("mensaje"));
        assertEquals("Solicitud incorrecta",
                handler().handleResponseStatus(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud incorrecta"), request())
                        .getBody().get("mensaje"));
        assertEquals("Ocurrió un error inesperado en el servidor",
                handler().handleResponseStatus(new ResponseStatusException(HttpStatusCode.valueOf(599), "No estándar"), request())
                        .getBody().get("mensaje"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - maneja método HTTP no soportado")
    void handleMethodNotSupported_Retorna405() {
        var response = handler().handleMethodNotSupported(
                new HttpRequestMethodNotSupportedException("PATCH"), request());

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("Método HTTP no permitido para este recurso", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - lee body con UTF-8 por defecto")
    void handleInvalidBody_LeeBodyConUtf8PorDefecto() {
        org.springframework.web.util.ContentCachingRequestWrapper wrapper =
                mock(org.springframework.web.util.ContentCachingRequestWrapper.class);
        when(wrapper.getContentAsByteArray()).thenReturn("{\"nombre\":".getBytes(StandardCharsets.UTF_8));
        when(wrapper.getCharacterEncoding()).thenReturn(null);
        when(wrapper.getRequestURI()).thenReturn("/handler-test/directo");

        Map<String, Object> body = handler()
                .handleInvalidBody(new HttpMessageNotReadableException("JSON inválido", null, null), wrapper)
                .getBody();

        assertEquals("{\"nombre\":", body.get("datosRecibidos"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - ignora referencias JSON sin nombre")
    void handleInvalidBody_IgnoraReferenciaSinNombre() {
        JsonMappingException mappingException =
                JsonMappingException.from((com.fasterxml.jackson.databind.DeserializationContext) null, "Tipo inválido");
        mappingException.prependPath(new JsonMappingException.Reference(new Object()));
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Error", mappingException, null);

        Map<String, Object> body = handler().handleInvalidBody(exception, request()).getBody();

        assertEquals(Map.of("request", "Tipo de dato inválido o estructura incorrecta"), body.get("errores"));
    }

    @Test
    @DisplayName("GlobalExceptionHandler - usa request si JsonMappingException no tiene path")
    void handleInvalidBody_UsaRequestSiMappingNoTienePath() {
        JsonMappingException mappingException =
                JsonMappingException.from((com.fasterxml.jackson.databind.DeserializationContext) null, "Tipo inválido");
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Error", mappingException, null);

        Map<String, Object> body = handler().handleInvalidBody(exception, request()).getBody();

        assertEquals(Map.of("request", "Tipo de dato inválido o estructura incorrecta"), body.get("errores"));
    }

    private GlobalExceptionHandler handler() {
        return new GlobalExceptionHandler(objectMapper);
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/handler-test/directo");
        return request;
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

        @GetMapping("/requiere-parametro")
        String requiereParametro(@RequestParam String nombre) {
            return nombre;
        }

        @GetMapping("/por-id/{id}")
        Long porId(@PathVariable Long id) {
            return id;
        }
    }
}
