package com.jose.sicov.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jose.sicov.model.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    
}
