package com.example.glstock.repository;

import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Buscar movimientos por producto
    List<Movimiento> findByProducto(Producto producto);

    // Buscar movimientos de los ultimos 10 días
    List<Movimiento> findTop10ByOrderByFechaDesc();

    // Buscar movimientos entre dos fechas especificas
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
