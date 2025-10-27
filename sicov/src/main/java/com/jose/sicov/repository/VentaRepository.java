package com.jose.sicov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.jose.sicov.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {


}