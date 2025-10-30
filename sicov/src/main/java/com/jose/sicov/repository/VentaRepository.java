package com.jose.sicov.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jose.sicov.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

     // Consulta para la búsqueda en la UI de "Gestión de Ventas" (por ID de Venta o Nombre del Cliente)
    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente c WHERE " +
           "CAST(v.id AS string) LIKE CONCAT('%', :query, '%') OR " + 
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Venta> searchVentas(@Param("query") String query, Pageable pageable);
}