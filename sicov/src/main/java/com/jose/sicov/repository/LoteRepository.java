package com.jose.sicov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.jose.sicov.model.Lote;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long>, JpaSpecificationExecutor<Lote> {

    // --- LÓGICA DE COMPRAS (ENTRADA: UPSERT) ---
    // Buscar un lote activo existente por Producto, Almacén y Número de Lote para sumar stock.
    Optional<Lote> findByProductoIdAndAlmacenIdAndNumeroLoteAndActivoTrue(
        Long productoId, Long almacenId, String numeroLote);

    // --- LÓGICA DE VENTAS (SALIDA FIFO/FEFO) ---
    // Consulta para obtener lotes con stock, ordenados por fecha de vencimiento ascendente (FEFO).
    @Query("SELECT l FROM Lote l WHERE l.producto.id = :productoId AND l.cantidadActual > 0 AND l.activo = true ORDER BY l.fechaVencimiento ASC, l.creadoEn ASC")
    List<Lote> findLotesDisponiblesParaVenta(
        @Param("productoId") Long productoId);
    
    // Lógica de Inventario
    /**
     * Consulta para obtener todos los lotes activos en inventario.
     * Ordena por nombre de producto y luego por fecha de vencimiento (ASC).
     */
    @Query("SELECT l FROM Lote l LEFT JOIN FETCH l.producto p WHERE l.activo = true ORDER BY p.nombre ASC, l.fechaVencimiento ASC")
    List<Lote> findAllActiveLotesOrderedByProductAndExpiry();
    
    
    @Modifying
    @Query("UPDATE Lote l SET l.cantidadActual = l.cantidadActual - :cantidad WHERE l.id = :loteId AND l.cantidadActual >= :cantidad")
    int descontarInventario(@Param("loteId") Long loteId, @Param("cantidad") Integer cantidad);

}
