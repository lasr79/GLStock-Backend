package com.example.glstock.controller;

import com.example.glstock.model.Movimiento;
import com.example.glstock.model.Usuario;
import com.example.glstock.service.MovimientoService;
import com.example.glstock.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;
    private final UsuarioService usuarioService;
    //Crear nuevos movimientos
    @PostMapping("/registrar")
    public ResponseEntity<Movimiento> registrarMovimiento(
            @RequestBody Movimiento movimiento,
            @AuthenticationPrincipal UserDetails userDetails) {

        String correo = userDetails.getUsername();
        Usuario usuario = usuarioService.findByCorreo(correo);
        movimiento.setUsuario(usuario);
        return ResponseEntity.ok(movimientoService.registrarMovimiento(movimiento));
    }
    //Busca los ultimos 10 movimientos agregados
    @GetMapping("/ultimos-10-movimientos")
    public ResponseEntity<List<Movimiento>> ultimos10Movimientos() {
        return ResponseEntity.ok(movimientoService.ultimos10Movimientos());
    }
    //Busca los movimientos por un rango de fechas dadas por el usuario
    @GetMapping("/rango")
    public ResponseEntity<List<Movimiento>> movimientosPorRango(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.movimientosEntreFechas(inicio, fin));
    }
}