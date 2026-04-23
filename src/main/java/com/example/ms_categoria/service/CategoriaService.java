package com.example.ms_categoria.service;

import com.example.ms_categoria.dto.CategoriaDto;
import java.util.List;

public interface CategoriaService {
    CategoriaDto crearCategoria(CategoriaDto categoriaDto);
    CategoriaDto obtenerCategoria(Long id);
    List<CategoriaDto> listarCategorias();
    CategoriaDto actualizarCategoria(Long id, CategoriaDto categoriaDto);
    void eliminarCategoria(Long id);
}