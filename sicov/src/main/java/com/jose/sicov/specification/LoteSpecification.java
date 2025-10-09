package com.jose.sicov.specification;

import com.jose.sicov.model.Lote;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class LoteSpecification {
    
    /**
     * Filtra los lotes basándose en múltiples parámetros de búsqueda.
     */
    public static Specification<Lote> filtrar(Long productoId, Long almacenId, String numeroLote, Boolean stockDisponible, Boolean vencido) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Filtro por Producto
            if (productoId != null) {
                // Navegación: Lote -> Producto -> id
                predicates.add(cb.equal(root.get("producto").get("id"), productoId));
            }

            // 2. Filtro por Almacén
            if (almacenId != null) {
                // Navegación: Lote -> Almacen -> id
                predicates.add(cb.equal(root.get("almacen").get("id"), almacenId));
            }

            // 3. Filtro por Número de Lote (Búsqueda parcial, insensible a mayúsculas/minúsculas)
            if (numeroLote != null && !numeroLote.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("numeroLote")), "%" + numeroLote.toLowerCase() + "%"));
            }

            // 4. Filtro por Stock Disponible (cantidadActual > 0)
            if (Boolean.TRUE.equals(stockDisponible)) {
                predicates.add(cb.greaterThan(root.get("cantidadActual"), 0));
            } else if (Boolean.FALSE.equals(stockDisponible)) {
                predicates.add(cb.equal(root.get("cantidadActual"), 0));
            }

            // 5. Filtro por Vencimiento (vence hoy o ya venció)
            if (Boolean.TRUE.equals(vencido)) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaVencimiento"), LocalDate.now()));
            }

            // Filtro base: Excluir eliminados
            predicates.add(cb.equal(root.get("eliminado"), false));

            // Devuelve la combinación de todas las condiciones (AND)
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}