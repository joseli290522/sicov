package com.jose.sicov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jose.sicov.model.Impuesto;
import com.jose.sicov.model.TipoImpuesto;

public interface ImpuestoRepository extends JpaRepository<Impuesto, Long> {

    Optional<Impuesto> findByNombre(String nombre);

    List<Impuesto> findByTipoAndActivoTrue(TipoImpuesto tipo);   

}
