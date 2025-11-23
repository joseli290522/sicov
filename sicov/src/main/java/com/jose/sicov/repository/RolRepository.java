package com.jose.sicov.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.sicov.model.Rol;
import com.jose.sicov.model.RolEnum;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(RolEnum nombre);
    
}
