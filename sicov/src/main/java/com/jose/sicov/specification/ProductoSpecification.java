package com.jose.sicov.specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import com.jose.sicov.model.Producto;

public class ProductoSpecification {

    public static Specification<Producto> filtrar(String categoria, Boolean activo, String nombre) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoria != null && !categoria.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("categoria").get("nombre"), categoria));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            predicates.add(cb.equal(root.get("eliminado"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
