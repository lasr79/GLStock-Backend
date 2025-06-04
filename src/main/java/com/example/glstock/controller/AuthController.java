package com.example.glstock.controller;

import com.example.glstock.model.Usuario;
import com.example.glstock.security.JwtService;
import com.example.glstock.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
//Metodo de login cifrando la contraseña verificando que coincida con el de la basede datos y generando el token de seguridad
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos) {
        String correo = datos.get("correo");
        String contrasena = datos.get("contrasena");
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorCorreo(correo);
        if (usuarioOpt.isPresent() && passwordEncoder.matches(contrasena, usuarioOpt.get().getContrasena())) {
            Usuario usuario = usuarioOpt.get();
            String token = jwtService.generateToken(usuario.getCorreo());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("correo", usuario.getCorreo());
            response.put("rol", usuario.getRol().name());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Correo o contraseña incorrectos");
        }
    }
}
