package com.example.ms_categoria.controller;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para administrar categorias usadas para clasificar productos del negocio.")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @PostMapping
    @Operation(summary = "Registrar categoria", description = "Crea una nueva categoria para clasificar productos dentro del sistema de Chinito Importaciones. La categoria queda disponible para asociarse a productos nuevos o existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de categoria invalidos")
    })
    public ResponseEntity<CategoriaDto> crear(@Valid @RequestBody CategoriaDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.crearCategoria(categoriaDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoria por id", description = "Obtiene el detalle de una categoria registrada usando su identificador unico. Es util para validar la clasificacion asociada a productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<CategoriaDto> obtener(@Parameter(description = "Identificador unico del recurso categoria.", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.obtenerCategoria(id));
    }

    @GetMapping
    @Operation(summary = "Listar categorias", description = "Obtiene todas las categorias registradas en el sistema. Estas categorias permiten organizar los productos del negocio segun su tipo o clasificacion.")
    @ApiResponse(responseCode = "200", description = "Listado de categorias obtenido correctamente")
    public ResponseEntity<List<CategoriaDto>> listar() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoria", description = "Modifica los datos de una categoria existente usando su identificador unico, manteniendo la misma ruta del recurso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<CategoriaDto> actualizar(@Parameter(description = "Identificador unico del recurso categoria.", example = "1") @PathVariable Long id, @Valid @RequestBody CategoriaDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(id, categoriaDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoria", description = "Elimina una categoria registrada en el sistema mediante su identificador. Antes de usarlo, verificar que la categoria no sea necesaria para productos activos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
    })
    public ResponseEntity<Void> eliminar(@Parameter(description = "Identificador unico del recurso categoria.", example = "1") @PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
