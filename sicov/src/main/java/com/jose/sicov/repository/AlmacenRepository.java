package com.jose.sicov.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.jose.sicov.model.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long>, JpaSpecificationExecutor<Almacen> {
    
    Optional<Almacen> findByNombre(String nombre);
}
