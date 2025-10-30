package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "detalles_venta")
@Getter @Setter
public class DetalleVenta extends Base {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_venta", precision = 10, scale = 2)
    private BigDecimal precioUnitarioVenta;
    
    // Trazabilidad: relaciona el detalle de la venta con los lotes que se consumieron.
    @OneToMany(mappedBy = "detalleVenta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteSalida> lotesConsumidos;
}