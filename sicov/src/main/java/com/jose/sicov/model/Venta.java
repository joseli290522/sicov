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

    @Override
    public VentaDTO getDto() {
        return VentaDTO.builder()
            .id(this.id)
            .clienteNombre(this.cliente != null ? this.cliente.getNombre() : null)
            .almacenNombre(this.almacen != null ? this.almacen.getNombre() : null)
            .fechaVenta(this.fechaVenta)
            .subtotal(this.subtotal)
            .totalFinal(this.totalFinal)
            .metodoPago(this.metodoPago)
            .detalles(this.detalles.stream().map(DetalleVenta::getDto).collect(Collectors.toList()))
            .build();
    }

    @Override
    public void setData(VentaDTO dto) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
}