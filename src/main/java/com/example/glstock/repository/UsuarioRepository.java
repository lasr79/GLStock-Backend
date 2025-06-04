package com.example.glstock.repository;


import com.example.glstock.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Busca un usuario exacto por su correo
    Optional<Usuario> findByCorreo(String correo);
    //Busca un usuario por nombre sin tener que poner el nombre completo
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    //Busca por correo sin poner el correo entero
    List<Usuario> findByCorreoContainingIgnoreCase(String correo);
}