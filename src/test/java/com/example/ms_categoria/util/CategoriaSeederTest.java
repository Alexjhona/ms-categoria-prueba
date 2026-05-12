package com.example.ms_categoria.util;

import com.example.ms_categoria.entity.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoriaSeederTest {

    @Test
    @DisplayName("Seeder - inserta categorías cuando la base está vacía")
    void run_BaseVacia_InsertaCategorias() {
        CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
        CategoriaSeeder seeder = new CategoriaSeeder(categoriaRepository);

        when(categoriaRepository.count()).thenReturn(0L);

        seeder.run();

        verify(categoriaRepository).count();
        verify(categoriaRepository, times(30)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Seeder - no inserta categorías cuando ya existen datos")
    void run_ConDatos_NoInsertaCategorias() {
        CategoriaRepository categoriaRepository = mock(CategoriaRepository.class);
        CategoriaSeeder seeder = new CategoriaSeeder(categoriaRepository);

        when(categoriaRepository.count()).thenReturn(5L);

        seeder.run();

        verify(categoriaRepository).count();
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
}