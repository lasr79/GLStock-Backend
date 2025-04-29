package com.example.glstock.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        String password = "admin123"; // Puedes cambiar esta clave
        String hash = new BCryptPasswordEncoder().encode(password);
        System.out.println("Contraseña: " + password);
        System.out.println("Hash cifrado: " + hash);
    }
}