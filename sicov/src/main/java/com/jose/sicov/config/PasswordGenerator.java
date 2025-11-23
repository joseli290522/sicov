package com.jose.sicov.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Contraseña que deseas usar
        String rawPassword = "password123"; 

        // Crea el encoder (debe coincidir con el de SecurityConfig)
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Genera el hash encriptado
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("------------------------------------------------------------------");
        System.out.println("Contraseña Plana: " + rawPassword);
        System.out.println("HASH BCrypt (Para SQL): " + hashedPassword);
        System.out.println("------------------------------------------------------------------");
    }
}