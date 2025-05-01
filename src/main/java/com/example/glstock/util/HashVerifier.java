package com.example.glstock.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashVerifier {
    public static void main(String[] args) {
        String rawPassword = "hola";
        String hashEnBD = "$2a$10$ocvXpt3IsM6yr9lbuiPibeGAszIlybUgsULTIvsxZWsv7IYzYRO2y"; // <--- el que te dio MySQL

        boolean coincide = new BCryptPasswordEncoder().matches(rawPassword, hashEnBD);
        System.out.println("¿Coinciden?: " + coincide);
    }
}
