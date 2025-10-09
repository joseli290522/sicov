package com.jose.sicov.specification;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import com.jose.sicov.model.Cliente;

public class ClienteSpecification {

    public static Specification<Cliente> filtrar(String nombre, String contacto, Boolean activo) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }

            if (contacto != null && !contacto.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("contacto")), "%" + contacto.toLowerCase() + "%"));
            }

            if (activo != null) {
                predicates.add(cb.equal(root.get("activo"), activo));
            }

            predicates.add(cb.equal(root.get("eliminado"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
}
