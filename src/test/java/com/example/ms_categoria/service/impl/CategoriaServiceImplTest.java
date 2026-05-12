package com.example.ms_categoria.service.impl;

import com.example.ms_categoria.dto.CategoriaDto;
import com.example.ms_categoria.entity.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    @Test
    @DisplayName("Crear categoría - guarda correctamente")
    void crearCategoria_GuardaCorrectamente() {
        CategoriaDto entrada = new CategoriaDto(null, "Cables USB");

        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria categoria = invocation.getArgument(0);
            categoria.setId(1L);
            return categoria;
        });

        CategoriaDto resultado = categoriaService.crearCategoria(entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Cables USB");

        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Obtener categoría - retorna categoría existente")
    void obtenerCategoria_RetornaCategoriaExistente() {
        Categoria categoria = new Categoria(1L, "Mouse Gaming");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaDto resultado = categoriaService.obtenerCategoria(1L);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Mouse Gaming");

        verify(categoriaRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener categoría - lanza excepción si no existe")
    void obtenerCategoria_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.obtenerCategoria(99L)
        );

        assertThat(exception.getMessage()).contains("Categoría no encontrada");

        verify(categoriaRepository).findById(99L);
    }

    @Test
    @DisplayName("Listar categorías - retorna lista ordenada")
    void listarCategorias_RetornaLista() {
        Categoria cat1 = new Categoria(1L, "Cables USB");
        Categoria cat2 = new Categoria(2L, "Cargadores Móvil");

        when(categoriaRepository.findAllByOrderByIdAsc()).thenReturn(List.of(cat1, cat2));

        List<CategoriaDto> resultado = categoriaService.listarCategorias();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Cables USB");
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(1).getNombre()).isEqualTo("Cargadores Móvil");

        verify(categoriaRepository).findAllByOrderByIdAsc();
    }

    @Test
    @DisplayName("Actualizar categoría - actualiza correctamente")
    void actualizarCategoria_ActualizaCorrectamente() {
        Categoria existente = new Categoria(1L, "Nombre Antiguo");
        CategoriaDto entrada = new CategoriaDto(null, "Nombre Nuevo");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaDto resultado = categoriaService.actualizarCategoria(1L, entrada);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Nombre Nuevo");

        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).save(existente);
    }

    @Test
    @DisplayName("Actualizar categoría - lanza excepción si no existe")
    void actualizarCategoria_NoExiste_LanzaExcepcion() {
        CategoriaDto entrada = new CategoriaDto(null, "Nueva Categoría");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.actualizarCategoria(99L, entrada)
        );

        assertThat(exception.getMessage()).contains("Categoría no encontrada");

        verify(categoriaRepository).findById(99L);
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Eliminar categoría - elimina si existe")
    void eliminarCategoria_Existe_EliminaCorrectamente() {
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        categoriaService.eliminarCategoria(1L);

        verify(categoriaRepository).existsById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar categoría - lanza excepción si no existe")
    void eliminarCategoria_NoExiste_LanzaExcepcion() {
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.eliminarCategoria(99L)
        );

        assertThat(exception.getMessage()).contains("No existe categoría");

        verify(categoriaRepository).existsById(99L);
        verify(categoriaRepository, never()).deleteById(anyLong());
    }
}