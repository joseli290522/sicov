package com.jose.sicov.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.jose.sicov.model.Almacen;
import jakarta.persistence.criteria.Predicate;

public class AlmacenSpecification {
    
    public static Specification<Almacen> filtrar(String nombre, String ubicacion, Boolean activo) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            if (ubicacion != null && !ubicacion.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("ubicacion")), "%" + ubicacion.toLowerCase() + "%"));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            predicates.add(cb.equal(root.get("eliminado"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
