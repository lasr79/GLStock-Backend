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

        // Aplicar lógica según el tipo de movimiento
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

        // Establecer la fecha si no viene del frontend
        if (movimiento.getFecha() == null) {
            movimiento.setFecha(LocalDateTime.now());
        }

        // Aquí podrías setear el usuario que realiza el movimiento si estás usando seguridad
        // movimiento.setUsuario(usuarioActual);

        // Guardar el movimiento
        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> buscarPorProducto(Producto producto) {
        return movimientoRepository.findByProducto(producto);
    }

    public List<Movimiento> movimientosUltimos10Dias() {
        LocalDateTime hace10dias = LocalDateTime.now().minusDays(10);
        return movimientoRepository.findByFechaAfter(hace10dias);
    }

    public List<Movimiento> movimientosEntreFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin);
    }
}
