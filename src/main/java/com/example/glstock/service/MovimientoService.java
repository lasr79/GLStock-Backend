package com.example.glstock.service;

import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Producto;
import com.example.glstock.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoService productoService;

    public Movimiento registrarMovimiento(Movimiento movimiento) {
        // Buscar el producto asociado
        Producto producto = productoService.buscarPorId(movimiento.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Selecciona el tipo de movimiento y con un control en caso de no tener stock
        switch (movimiento.getTipo()) {
            case ENTRADA -> {
                producto.setCantidad(producto.getCantidad() + movimiento.getCantidad());
            }
            case SALIDA -> {
                if (producto.getCantidad() < movimiento.getCantidad()) {
                    throw new RuntimeException("Stock insuficiente");
                }
                producto.setCantidad(producto.getCantidad() - movimiento.getCantidad());
            }
        }

        // Actualizar producto en base de datos
        productoService.guardar(producto);

        // Asociar el producto actualizado al movimiento
        movimiento.setProducto(producto);

        // Establecer la fecha si no viene del frontend en android studio
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDateTime.now());
        }
        return movimientoRepository.save(movimiento);
    }
    //Busca los ultimos movimientos registrados por fecha ingreso
    public List<Movimiento> ultimos10Movimientos() {
        return movimientoRepository.findTop10ByOrderByFechaDesc();
    }
    //Busca movimientos por rango de fechas dadas por el usuario
    public List<Movimiento> movimientosEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin);
    }
}
