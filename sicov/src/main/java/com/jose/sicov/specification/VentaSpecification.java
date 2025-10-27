package com.jose.sicov.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.jose.sicov.filter.VentaFilter;
import com.jose.sicov.model.Cliente;
import com.jose.sicov.model.Venta;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;


public class VentaSpecification {
    public static Specification<Venta> byFilter(VentaFilter filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Rango de Fechas
            if (filter.getFechaDesde() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("fechaVenta"), filter.getFechaDesde()));
            }
            if (filter.getFechaHasta() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("fechaVenta"), filter.getFechaHasta()));
            }
            
            // 2. Rango de Total Final
            if (filter.getTotalMinimo() != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("totalFinal"), filter.getTotalMinimo()));
            }
            if (filter.getTotalMaximo() != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("totalFinal"), filter.getTotalMaximo()));
            }

            // 3. Nombre de Cliente (Join)
            if (filter.getClienteNombre() != null && !filter.getClienteNombre().trim().isEmpty()) {
                String likePattern = "%" + filter.getClienteNombre().trim().toLowerCase() + "%";
                Join<Venta, Cliente> clienteJoin = root.join("cliente");
                
                Predicate nombreMatch = builder.like(builder.lower(clienteJoin.get("nombre")), likePattern);
                Predicate apellidoMatch = builder.like(builder.lower(clienteJoin.get("apellido")), likePattern);
                
                predicates.add(builder.or(nombreMatch, apellidoMatch));
            }
            
            // Filtro por defecto: ventas activas
            predicates.add(builder.isTrue(root.get("activo")));

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
