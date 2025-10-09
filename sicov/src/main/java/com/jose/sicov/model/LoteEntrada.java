package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "entradas_lote")
@Data
@NoArgsConstructor
public class LoteEntrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Lote afectado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote; 

    @Column(name = "cantidad_entrada", nullable = false)
    private Integer cantidadEntrada;
    
    @Column(name = "referencia_entrada", nullable = false, length = 100)
    private String referenciaEntrada; // Guía/Remisión
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}