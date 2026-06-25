package com.example.ms_categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaDto {
    private Long id;

    @NotBlank(message = "Campo obligatorio")
    @Size(max = 120, message = "No debe superar 120 caracteres")
    private String nombre;

    @Size(max = 1000, message = "No debe superar 1000 caracteres")
    private String imagen;

    public CategoriaDto() {
    }

    public CategoriaDto(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public CategoriaDto(Long id, String nombre, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
