package com.jose.sicov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.sicov.model.DetalleCompra;

public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Long> {}