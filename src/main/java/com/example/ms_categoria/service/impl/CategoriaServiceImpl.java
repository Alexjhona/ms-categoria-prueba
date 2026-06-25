package com.example.ms_categoria.service.impl;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.entity.Categoria;
import com.example.ms_categoria.exception.ConflictoRecursoException;
import com.example.ms_categoria.exception.RecursoNoEncontradoException;
import com.example.ms_categoria.repository.CategoriaRepository;
import com.example.ms_categoria.service.CategoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public CategoriaDto crearCategoria(CategoriaDto categoriaDto) {
        if (categoriaRepository.existsByNombre(categoriaDto.getNombre())) {
            throw new ConflictoRecursoException("Ya existe un registro con ese nombre");
        }

        Categoria cat = new Categoria();
        cat.setNombre(categoriaDto.getNombre());
        cat.setImagen(categoriaDto.getImagen());
        Categoria guardada = categoriaRepository.save(cat);
        return mapToDto(guardada);
    }

    @Override
    public CategoriaDto obtenerCategoria(Long id) {
        Categoria cat = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));
        return mapToDto(cat);
    }

    @Override
    public List<CategoriaDto> listarCategorias() {
        return categoriaRepository.findAllByOrderByIdAsc()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CategoriaDto actualizarCategoria(Long id, CategoriaDto categoriaDto) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con id: " + id));

        if (categoriaRepository.existsByNombreAndIdNot(categoriaDto.getNombre(), id)) {
            throw new ConflictoRecursoException("Ya existe otro registro con ese nombre");
        }

        existente.setNombre(categoriaDto.getNombre());
        existente.setImagen(categoriaDto.getImagen());
        Categoria actualizada = categoriaRepository.save(existente);
        return mapToDto(actualizada);
    }

    @Override
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe categoría con id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaDto mapToDto(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre(), c.getImagen());
    }
}
