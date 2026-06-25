package com.example.ms_categoria.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String MENSAJE_VALIDACION = "Se encontraron errores de validación";
    private static final String MENSAJE_NOT_FOUND = "No se encontró el recurso solicitado";
    private static final String MENSAJE_CONFLICT = "El registro ya existe o genera conflicto";
    private static final String MENSAJE_ERROR_INTERNO = "Ocurrió un error inesperado en el servidor";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), mensajeClaro(error.getDefaultMessage())));
        exception.getBindingResult().getGlobalErrors()
                .forEach(error -> errores.putIfAbsent(error.getObjectName(), mensajeClaro(error.getDefaultMessage())));

        Map<String, Object> response = baseResponse(HttpStatus.BAD_REQUEST, MENSAJE_VALIDACION, request);
        response.put("datosRecibidos", datosRecibidos(request, exception.getBindingResult().getTarget()));
        response.put("errores", errores);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        errores.put(campoDesdeJsonMapping(exception), mensajeBodyInvalido(exception));

        Map<String, Object> response = baseResponse(HttpStatus.BAD_REQUEST, MENSAJE_VALIDACION, request);
        response.put("datosRecibidos", datosRecibidos(request, null));
        response.put("errores", errores);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violation ->
                errores.putIfAbsent(nombreCampo(violation.getPropertyPath().toString()), mensajeClaro(violation.getMessage())));

        Map<String, Object> response = baseResponse(HttpStatus.BAD_REQUEST, MENSAJE_VALIDACION, request);
        response.put("datosRecibidos", datosRecibidos(request, null));
        response.put("errores", errores);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, Object>> handleInvalidParameter(Exception exception, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        if (exception instanceof MissingServletRequestParameterException missing) {
            errores.put(missing.getParameterName(), "Campo obligatorio");
        } else if (exception instanceof MethodArgumentTypeMismatchException mismatch) {
            errores.put(mismatch.getName(), "Tipo de dato inválido");
        }

        Map<String, Object> response = baseResponse(HttpStatus.BAD_REQUEST, MENSAJE_VALIDACION, request);
        response.put("datosRecibidos", datosRecibidos(request, null));
        response.put("errores", errores);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({
            RecursoNoEncontradoException.class,
            EntityNotFoundException.class,
            NoSuchElementException.class,
            NoHandlerFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFound(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, MENSAJE_NOT_FOUND, request);
    }

    @ExceptionHandler({
            ConflictoRecursoException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflict(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, MENSAJE_CONFLICT, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        if (esConflicto(exception.getMessage())) {
            return buildErrorResponse(HttpStatus.CONFLICT, MENSAJE_CONFLICT, request);
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensajeClaro(exception.getMessage()), request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request) {
        HttpStatus status = resolverHttpStatus(exception.getStatusCode());
        return buildErrorResponse(status, mensajePorStatus(status, exception.getReason()), request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no permitido para este recurso", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleInternalError(Exception exception, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, MENSAJE_ERROR_INTERNO, request);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status,
            String mensaje,
            HttpServletRequest request) {
        return ResponseEntity.status(status).body(baseResponse(status, mensaje, request));
    }

    private Map<String, Object> baseResponse(HttpStatus status, String mensaje, HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now().withNano(0).format(TIMESTAMP_FORMATTER));
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("mensaje", mensaje);
        response.put("ruta", request.getRequestURI());

        return response;
    }

    private Object datosRecibidos(HttpServletRequest request, Object fallback) {
        String body = requestBody(request);
        if (!body.isBlank()) {
            try {
                return objectMapper.readValue(body, Object.class);
            } catch (JsonProcessingException exception) {
                return body;
            }
        }

        if (fallback != null) {
            return objectMapper.convertValue(fallback, Object.class);
        }

        return new LinkedHashMap<>();
    }

    private String requestBody(HttpServletRequest request) {
        if (!(request instanceof ContentCachingRequestWrapper wrapper)) {
            return "";
        }

        byte[] content = wrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }

        Charset charset = request.getCharacterEncoding() == null
                ? StandardCharsets.UTF_8
                : Charset.forName(request.getCharacterEncoding());
        return new String(content, charset);
    }

    private String campoDesdeJsonMapping(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        if (cause instanceof JsonMappingException mappingException && !mappingException.getPath().isEmpty()) {
            return mappingException.getPath().stream()
                    .map(this::nombreReferencia)
                    .filter(Objects::nonNull)
                    .reduce((parent, child) -> parent + "." + child)
                    .orElse("request");
        }

        return "request";
    }

    private String nombreReferencia(JsonMappingException.Reference reference) {
        if (reference.getFieldName() != null) {
            return reference.getFieldName();
        }
        if (reference.getIndex() >= 0) {
            return "[" + reference.getIndex() + "]";
        }
        return null;
    }

    private String mensajeBodyInvalido(HttpMessageNotReadableException exception) {
        Throwable cause = exception.getMostSpecificCause();
        if (cause instanceof JsonMappingException) {
            return "Tipo de dato inválido o estructura incorrecta";
        }

        return "El cuerpo de la solicitud no tiene un formato JSON válido";
    }

    private String nombreCampo(String propertyPath) {
        int dotIndex = propertyPath.lastIndexOf('.');
        return dotIndex >= 0 ? propertyPath.substring(dotIndex + 1) : propertyPath;
    }

    private String mensajeClaro(String mensaje) {
        return mensaje == null || mensaje.isBlank() ? "Valor inválido" : mensaje;
    }

    private boolean esConflicto(String mensaje) {
        if (mensaje == null) {
            return false;
        }

        String normalizado = mensaje.toLowerCase();
        return normalizado.contains("existe")
                || normalizado.contains("duplic")
                || normalizado.contains("conflict")
                || normalizado.contains("unique");
    }

    private HttpStatus resolverHttpStatus(HttpStatusCode statusCode) {
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        return status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    }

    private String mensajePorStatus(HttpStatus status, String reason) {
        return switch (status) {
            case NOT_FOUND -> MENSAJE_NOT_FOUND;
            case CONFLICT -> MENSAJE_CONFLICT;
            case INTERNAL_SERVER_ERROR -> MENSAJE_ERROR_INTERNO;
            default -> mensajeClaro(reason);
        };
    }
}
