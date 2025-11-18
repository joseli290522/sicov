package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "ventas")
@Getter @Setter
public class Venta extends Base implements IMapper<VentaDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "fecha_venta", nullable = false)
    private LocalDate fechaVenta = LocalDate.now();

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalFinal; 

    @Column(name = "metodo_pago")
    private String metodoPago;
    
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @ManyToOne(optional = false)
    @JoinColumn(name = "impuesto_iva_id")
    private Impuesto impuestoIVA;

    @ManyToOne
    @JoinColumn(name = "impuesto_ieps_id")
    private Impuesto impuestoIEPS;

    @Column(name = "monto_recibido") 
    private BigDecimal montoRecibido;

    @Override
    public VentaDTO getDto() {
        return VentaDTO.builder()
            .id(this.id)

            .clienteId(this.cliente != null ? this.cliente.getId() : null)
            .clienteNombre(this.cliente != null ? this.cliente.getNombre() : null)

            .almacenId(this.almacen != null ? this.almacen.getId() : null)
            .almacenNombre(this.almacen != null ? this.almacen.getNombre() : null)

            .fechaVenta(this.fechaVenta)
            .subtotal(this.subtotal)
            .totalFinal(this.totalFinal)
            .metodoPago(this.metodoPago)
            
            .detalles(this.detalles.stream().map(DetalleVenta::getDto).collect(Collectors.toList()))

            .impuestoIvaId(this.impuestoIVA != null ? this.impuestoIVA.getId() : null)
            .ivaPorcentaje(this.impuestoIVA != null ? this.impuestoIVA.getPorcentaje() : BigDecimal.ZERO)

            .impuestoIepsId(this.impuestoIEPS != null ? this.impuestoIEPS.getId() : null)
            .iepsPorcentaje(this.impuestoIEPS != null ? this.impuestoIEPS.getPorcentaje() : BigDecimal.ZERO)

            .montoRecibido(this.montoRecibido)

            .build();
    }

    @Override
    public void setData(VentaDTO dto) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
}