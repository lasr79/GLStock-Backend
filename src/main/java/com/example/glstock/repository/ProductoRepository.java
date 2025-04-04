package com.example.glstock.repository;

import com.example.glstock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por nombre exacto
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por categoría exacta (puedes usar contains si quieres más flexible)
    List<Producto> findByCategoriaIgnoreCase(String categoria);
}
