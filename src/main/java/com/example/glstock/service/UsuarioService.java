package com.example.glstock.service;

import com.example.glstock.model.Usuario;
import com.example.glstock.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder; // 👈 Agregamos el codificador de contraseñas
    //busqueda del usuario poniendo el correo entero
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    //Busqueda de usuario por correo poniendo cualquier inicial de correo
    public List<Usuario> buscarPorCorreoPoniendoCualquierInicial(String correo) {
        return usuarioRepository.findByCorreoContainingIgnoreCase(correo);
    }
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Usuario guardar(Usuario usuario) {
        // 👇 Codificamos la contraseña antes de guardar
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}

