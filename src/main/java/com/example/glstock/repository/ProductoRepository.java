package com.example.glstock.repository;

import com.example.glstock.model.Categoria;
import com.example.glstock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos cuyo nombre contenga un texto (ignore case)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar productos por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // Productos con menor cantidad de stock (ej: top 5)
    List<Producto> findTop5ByOrderByCantidadAsc();

    // Últimos productos agregados 5 productos agregados
    List<Producto> findTop5ByOrderByFechaIngresoDesc();

    List<Producto> findByCantidadLessThan(int cantidad);

    List<Producto> findByCategoriaNombreContainingIgnoreCase(String nombreCategoria);
}