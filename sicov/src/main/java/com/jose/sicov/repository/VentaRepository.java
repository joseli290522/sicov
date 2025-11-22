package com.jose.sicov.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jose.sicov.model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long>, JpaSpecificationExecutor<Venta> {

     // Consulta para la búsqueda en la UI de "Gestión de Ventas" (por ID de Venta o Nombre del Cliente)

    // MODIFICADO: Añadido el parámetro :date (LocalDate)
    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente c WHERE " +
           // Cláusula de búsqueda (ID o Nombre del Cliente)
           "(CAST(v.id AS string) LIKE CONCAT('%', :query, '%') OR " + 
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           // Cláusula de filtro de fecha (Opcional)
           "(:date IS NULL OR v.fechaVenta = :date)") 
    Page<Venta> searchVentas(
            @Param("query") String query, 
            @Param("date") LocalDate date,
            Pageable pageable);
}