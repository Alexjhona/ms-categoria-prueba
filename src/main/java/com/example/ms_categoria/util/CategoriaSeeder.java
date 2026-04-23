package com.example.ms_categoria.util;

import com.example.ms_categoria.entity.Categoria;
import com.example.ms_categoria.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoriaSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;

    public CategoriaSeeder(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) {
        // Si no hay categorías aun en la BD, inserta todas estas:
        if (categoriaRepository.count() == 0) {
            Categoria cat1  = new Categoria(null, "Cables USB");
            Categoria cat2  = new Categoria(null, "Cargadores Móvil");
            Categoria cat3  = new Categoria(null, "Cargadores Laptop");
            Categoria cat4  = new Categoria(null, "Adaptadores HDMI");
            Categoria cat5  = new Categoria(null, "Adaptadores USB-C");
            Categoria cat6  = new Categoria(null, "Fundas y Carcasas");
            Categoria cat7  = new Categoria(null, "Audífonos Inalámbricos");
            Categoria cat8  = new Categoria(null, "Audífonos con Cable");
            Categoria cat9  = new Categoria(null, "Teclados Mecánicos");
            Categoria cat10 = new Categoria(null, "Teclados de Membrana");
            Categoria cat11 = new Categoria(null, "Mouse Gaming");
            Categoria cat12 = new Categoria(null, "Mouse Óptico");
            Categoria cat13 = new Categoria(null, "Monitores LED");
            Categoria cat14 = new Categoria(null, "Micrófonos USB");
            Categoria cat15 = new Categoria(null, "Cámaras Web");
            Categoria cat16 = new Categoria(null, "Bases de Carga Inalámbrica");
            Categoria cat17 = new Categoria(null, "Soportes para Laptop");
            Categoria cat18 = new Categoria(null, "Baterías Externas (Power Bank)");
            Categoria cat19 = new Categoria(null, "Protectores de Pantalla");
            Categoria cat20 = new Categoria(null, "Almacenamiento Externo (Discos Duros/SSD)");
            Categoria cat21 = new Categoria(null, "Parlantes Bluetooth");
            Categoria cat22 = new Categoria(null, "Parlantes con Cable");
            Categoria cat23 = new Categoria(null, "Memorias para Celular (MicroSD)");
            Categoria cat24 = new Categoria(null, "Memorias USB");
            Categoria cat25 = new Categoria(null, "Routers y Modems");
            Categoria cat26 = new Categoria(null, "Estabilizadores Eléctricos");
            Categoria cat27 = new Categoria(null, "Switches de Red");
            Categoria cat28 = new Categoria(null, "Cargadores Inalámbricos");
            Categoria cat29 = new Categoria(null, "Bocinas para Computadora");
            Categoria cat30 = new Categoria(null, "Repuestos y Accesorios Varios");

            categoriaRepository.save(cat1);
            categoriaRepository.save(cat2);
            categoriaRepository.save(cat3);
            categoriaRepository.save(cat4);
            categoriaRepository.save(cat5);
            categoriaRepository.save(cat6);
            categoriaRepository.save(cat7);
            categoriaRepository.save(cat8);
            categoriaRepository.save(cat9);
            categoriaRepository.save(cat10);
            categoriaRepository.save(cat11);
            categoriaRepository.save(cat12);
            categoriaRepository.save(cat13);
            categoriaRepository.save(cat14);
            categoriaRepository.save(cat15);
            categoriaRepository.save(cat16);
            categoriaRepository.save(cat17);
            categoriaRepository.save(cat18);
            categoriaRepository.save(cat19);
            categoriaRepository.save(cat20);
            categoriaRepository.save(cat21);
            categoriaRepository.save(cat22);
            categoriaRepository.save(cat23);
            categoriaRepository.save(cat24);
            categoriaRepository.save(cat25);
            categoriaRepository.save(cat26);
            categoriaRepository.save(cat27);
            categoriaRepository.save(cat28);
            categoriaRepository.save(cat29);
            categoriaRepository.save(cat30);

            System.out.println("Datos de categorías insertados correctamente.");
        } else {
            System.out.println("Las categorías ya existen, no se insertaron datos.");
        }
    }
}