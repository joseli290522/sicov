package com.jose.sicov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.jose.sicov.model.Lote;
import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long>, JpaSpecificationExecutor<Lote> {

    // 1. CLAVE: Obtener stock disponible para la venta
    // Spring JPA entiende 'productoId' como la ID de la entidad relacionada
    List<Lote> findByProductoIdAndCantidadActualGreaterThanAndEliminadoFalse(Long productoId, Integer cantidad);
    
    // 2. Para la entrada: Busca si el lote existe por sus 3 claves
    Optional<Lote> findByProductoIdAndAlmacenIdAndNumeroLoteAndEliminadoFalse(
        Long productoId, Long almacenId, String numeroLote);
}