package com.jose.sicov.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class Base {

    @Column(nullable = false)
    protected Boolean activo = true;

    @Column(nullable = false)
    protected Boolean eliminado = false;

    @CreationTimestamp
    @Column(updatable = false)
    protected LocalDateTime creadoEn;

    @UpdateTimestamp
    protected LocalDateTime actualizadoEn;
}
