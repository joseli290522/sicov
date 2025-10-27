package com.jose.sicov.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "ventas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Venta extends Base implements IMapper<VentaDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago; 

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva_porcentaje", precision = 5, scale = 2)
    private BigDecimal ivaPorcentaje; 

    @Column(name = "ieps_porcentaje", precision = 5, scale = 2)
    private BigDecimal iepsPorcentaje;

    @Column(name = "descuento_monto", precision = 10, scale = 2)
    private BigDecimal descuentoMonto = BigDecimal.ZERO; 

    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal;

    @Column(name = "monto_recibido", precision = 10, scale = 2)
    private BigDecimal montoRecibido = BigDecimal.ZERO; 

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @Override
    public void setData(VentaDTO dto) {
        this.setFechaVenta(dto.getFechaVenta() != null ? dto.getFechaVenta() : LocalDate.now());
        this.setMetodoPago(dto.getMetodoPago());
        this.setSubtotal(dto.getSubtotal());
        this.setIvaPorcentaje(dto.getIvaPorcentaje());
        this.setIepsPorcentaje(dto.getIepsPorcentaje());
        this.setDescuentoMonto(dto.getDescuentoMonto());
        this.setTotalFinal(dto.getTotalFinal());
        this.setMontoRecibido(dto.getMontoRecibido());
    }

    @Override
    public VentaDTO getDto() {
        return VentaDTO.builder()
            .id(this.id)
            .clienteId(this.cliente != null ? this.cliente.getId() : null)
            .clienteNombre(this.cliente != null ? this.cliente.getNombre(): null)
            .fechaVenta(this.fechaVenta)
            .subtotal(this.subtotal)
            .ivaPorcentaje(this.ivaPorcentaje)
            .iepsPorcentaje(this.iepsPorcentaje)
            .descuentoMonto(this.descuentoMonto)
            .totalFinal(this.totalFinal)
            .metodoPago(this.metodoPago)
            .montoRecibido(this.montoRecibido)
            // Campos de Base
            .activo(this.activo)
            .eliminado(this.eliminado)
            .creadoEn(this.creadoEn)
            .actualizadoEn(this.actualizadoEn)
            .build();
    }
}