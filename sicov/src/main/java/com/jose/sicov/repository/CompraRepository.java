package com.jose.sicov.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jose.sicov.model.Compra;

public interface CompraRepository extends JpaRepository<Compra, Long> {

    @Query("SELECT c FROM Compra c WHERE " +
           // Cláusula de búsqueda por Proveedor (Query de texto)
           // Si :query es NULL, se evalúa como TRUE, saltando la búsqueda LIKE.
           "(:query IS NULL OR LOWER(c.proveedor.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           // Cláusula de filtro de fecha (Opcional)
           // Si :date es NULL, se evalúa como TRUE, saltando el filtro de fecha.
           "(:date IS NULL OR c.fechaCompra = :date)") 
    Page<Compra> searchCompras(
            @Param("query") String query, 
            @Param("date") LocalDate date,
            Pageable pageable);
}