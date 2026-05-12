package com.example.ms_categoria.repository;

import com.example.ms_categoria.config.MySQLTestContainer;
import com.example.ms_categoria.entity.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("tc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Pruebas de integración - CategoriaRepository con MySQL real")
class CategoriaRepositoryIntegrationTest {

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MySQLTestContainer.INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", MySQLTestContainer.INSTANCE::getUsername);
        registry.add("spring.datasource.password", MySQLTestContainer.INSTANCE::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria categoriaGuardada;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        categoriaGuardada = categoriaRepository.save(new Categoria(null, "Cargadores"));
    }

    @Order(1)
    @Test
    @DisplayName("Guardar categoría - debe persistir y generar id")
    void guardarCategoria_DebePersistirConId() {
        Categoria nueva = new Categoria(null, "Audífonos");

        Categoria guardada = categoriaRepository.save(nueva);

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Audífonos");
    }

    @Order(2)
    @Test
    @DisplayName("Buscar categoría por id - retorna categoría existente")
    void buscarPorId_RetornaCategoriaExistente() {
        Optional<Categoria> resultado = categoriaRepository.findById(categoriaGuardada.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Cargadores");
    }

    @Order(3)
    @Test
    @DisplayName("Listar categorías - retorna todas ordenadas por id")
    void listarCategorias_RetornaListaOrdenada() {
        categoriaRepository.save(new Categoria(null, "Cables USB"));

        List<Categoria> categorias = categoriaRepository.findAllByOrderByIdAsc();

        assertThat(categorias).hasSize(2);
        assertThat(categorias)
                .extracting(Categoria::getNombre)
                .containsExactly("Cargadores", "Cables USB");
    }

    @Order(4)
    @Test
    @DisplayName("Actualizar categoría - persiste nuevo nombre")
    void actualizarCategoria_PersisteCambios() {
        categoriaGuardada.setNombre("Cargadores Actualizados");

        Categoria actualizada = categoriaRepository.save(categoriaGuardada);

        assertThat(actualizada.getId()).isEqualTo(categoriaGuardada.getId());
        assertThat(actualizada.getNombre()).isEqualTo("Cargadores Actualizados");
    }

    @Order(5)
    @Test
    @DisplayName("Eliminar categoría - ya no debe existir")
    void eliminarCategoria_NoExisteDespues() {
        Long id = categoriaGuardada.getId();

        categoriaRepository.deleteById(id);

        assertThat(categoriaRepository.findById(id)).isEmpty();
    }
}