package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class SalidaLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // El lote de donde se tomó el stock
    @ManyToOne
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote; 

    // Referencia al DetalleVenta que causó esta salida
    @OneToOne
    @JoinColumn(name = "detalle_venta_id", nullable = false)
    private DetalleVenta detalleVenta; 

    private int cantidad; // Cantidad que salió
    
    private LocalDateTime fechaSalida = LocalDateTime.now();
    
}