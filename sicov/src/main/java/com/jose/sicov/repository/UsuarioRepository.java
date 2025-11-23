package com.jose.sicov.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.sicov.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByUsername(String username);
}