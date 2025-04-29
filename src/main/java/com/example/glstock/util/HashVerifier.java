package com.example.glstock.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class HashVerifier {
    public static void main(String[] args) {
        String rawPassword = "admin123";
        String hashEnBD = "$2a$10$b66tBSG/AcWWVoWOWf44MukAPZ0I0ljuWXAgI7t4vtXxLKEP5tnZu"; // <--- el que te dio MySQL

        boolean coincide = new BCryptPasswordEncoder().matches(rawPassword, hashEnBD);
        System.out.println("¿Coinciden?: " + coincide);
    }
}
