package com.example.glstock.repository;

import com.example.glstock.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Últimos 10 días
    List<Movimiento> findByFechaAfterOrderByFechaDesc(LocalDateTime fechaDesde);

    // Por rango personalizado
    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);
}
