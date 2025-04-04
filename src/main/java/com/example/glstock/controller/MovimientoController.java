package com.example.glstock.controller;

import com.example.glstock.model.Movimiento;
import com.example.glstock.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    // Registrar entrada o salida
    @PostMapping
    public ResponseEntity<Movimiento> registrar(@RequestBody Movimiento movimiento) {
        return ResponseEntity.ok(movimientoService.registrarMovimiento(movimiento));
    }

    // Listar todos los movimientos
    @GetMapping
    public List<Movimiento> listar() {
        return movimientoService.listarTodos();
    }

    // Últimos 10 días
    @GetMapping("/ultimos-10-dias")
    public List<Movimiento> ultimos10Dias() {
        return movimientoService.obtenerUltimos10Dias();
    }

    // Por rango personalizado (fecha formato ISO: yyyy-MM-ddTHH:mm:ss)
    @GetMapping("/filtrar/por-fechas")
    public List<Movimiento> porRangoDeFechas(
            @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta
    ) {
        return movimientoService.filtrarPorRango(desde, hasta);
    }
}
