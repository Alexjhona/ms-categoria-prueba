package com.example.ms_categoria.service.impl;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.entity.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
import com.example.ms_categoria.service.CategoriaService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public CategoriaDto crearCategoria(CategoriaDto categoriaDto) {
        Categoria cat = new Categoria();
        cat.setNombre(categoriaDto.getNombre());
        Categoria guardada = categoriaRepository.save(cat);
        return mapToDto(guardada);
    }

    @Override
    public CategoriaDto obtenerCategoria(Long id) {
        Categoria cat = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + id));
        return mapToDto(cat);
    }

    @Override
    public List<CategoriaDto> listarCategorias() {
        return categoriaRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoriaDto actualizarCategoria(Long id, CategoriaDto categoriaDto) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con id: " + id));
        existente.setNombre(categoriaDto.getNombre());
        Categoria actualizada = categoriaRepository.save(existente);
        return mapToDto(actualizada);
    }

    @Override
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe categoría con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaDto mapToDto(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre());
    }
}