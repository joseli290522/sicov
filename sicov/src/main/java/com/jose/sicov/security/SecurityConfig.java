package com.jose.sicov.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Importaciones para CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List; // Importar List

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // URL de tu frontend (IMPORTANTE: Debe ser la URL exacta que aparece en el error de CORS)
    private static final String FRONTEND_ORIGIN = "https://ideal-funicular-x7wrx649r5v3ppwv-9000.app.github.dev";

    
    // 🚨 1. BEAN PARA LA CONFIGURACIÓN DE CORS 🚨
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 1. Establecer el origen permitido (tu frontend)
        configuration.setAllowedOrigins(List.of(FRONTEND_ORIGIN, "http://localhost:9000"));
        
        // 2. Establecer métodos (incluyendo OPTIONS, que es el preflight)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 3. Permitir todas las cabeceras (incluyendo Authorization para el JWT)
        configuration.setAllowedHeaders(List.of("*"));
        
        // 4. Permitir credenciales (necesario si se manejan cookies, aunque no es el caso de JWT, es buena práctica)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a todas las rutas de la API
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
    
    // Define el Password Encoder (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Define el Authentication Manager (necesario para el AuthController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    // Define el Filtro JWT como Bean para la inyección de sus dependencias internas
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 🚨 Habilitar CORS y usar la configuración definida arriba 🚨
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .csrf(AbstractHttpConfigurer::disable)
            // Deshabilitar la gestión de sesiones (Stateless para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // Permitir acceso al endpoint de login
                .requestMatchers("/api/v1/auth/**").permitAll() 
                // Requerir autenticación para endpoints protegidos (ej. compras/ventas)
                .requestMatchers("/api/v1/**").authenticated() 
                .anyRequest().permitAll()
            );

        // Añadir el filtro JWT a la cadena de seguridad ANTES del filtro estándar de login
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}