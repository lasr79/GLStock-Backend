package com.example.glstock.service;

import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Producto;
import com.example.glstock.repository.MovimientoRepository;
import com.example.glstock.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Registrar entrada o salida
    public Movimiento registrarMovimiento(Movimiento movimiento) {
        Producto producto = productoRepository.findById(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (movimiento.getTipo().name().equalsIgnoreCase("entrada")) {
            producto.setCantidad(producto.getCantidad() + movimiento.getCantidad());
        } else if (movimiento.getTipo().name().equalsIgnoreCase("salida")) {
            if (producto.getCantidad() < movimiento.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para salida");
            }
            producto.setCantidad(producto.getCantidad() - movimiento.getCantidad());
        }

        productoRepository.save(producto);
        movimiento.setFecha(LocalDateTime.now());
        return movimientoRepository.save(movimiento);
    }

    // Listar todos
    public List<Movimiento> listarTodos() {
        return movimientoRepository.findAll();
    }

    // Últimos 10 días
    public List<Movimiento> obtenerUltimos10Dias() {
        LocalDateTime hace10Dias = LocalDateTime.now().minusDays(10);
        return movimientoRepository.findByFechaAfterOrderByFechaDesc(hace10Dias);
    }

    // Por rango personalizado
    public List<Movimiento> filtrarPorRango(LocalDateTime desde, LocalDateTime hasta) {
        return movimientoRepository.findByFechaBetweenOrderByFechaDesc(desde, hasta);
    }
}
