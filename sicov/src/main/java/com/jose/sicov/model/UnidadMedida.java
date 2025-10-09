package com.jose.sicov.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "unidades_medida")
@Getter
@Setter
public class UnidadMedida extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(length = 10)
    private String abreviatura;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(nullable = false)
    private Boolean eliminado = false;
}
