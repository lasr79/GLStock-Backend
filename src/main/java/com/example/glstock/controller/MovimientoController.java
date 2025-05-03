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
    @PostMapping("/registrar")
    public ResponseEntity<Movimiento> registrarMovimiento(
            @RequestBody Movimiento movimiento,
            @AuthenticationPrincipal UserDetails userDetails) {

        String correo = userDetails.getUsername(); // recupera el correo del token
        Usuario usuario = usuarioService.findByCorreo(correo); // obtiene el usuario

        movimiento.setUsuario(usuario);
        return ResponseEntity.ok(movimientoService.registrarMovimiento(movimiento));
    }

    @GetMapping("/ultimos-10-dias")
    public ResponseEntity<List<Movimiento>> movimientosUltimos10Dias() {
        return ResponseEntity.ok(movimientoService.movimientosUltimos10Dias());
    }

    @GetMapping("/rango")
    public ResponseEntity<List<Movimiento>> movimientosPorRango(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        return ResponseEntity.ok(movimientoService.movimientosEntreFechas(inicio, fin));
    }
}