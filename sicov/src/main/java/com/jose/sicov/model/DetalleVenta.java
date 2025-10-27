package com.jose.sicov.model;

import com.jose.sicov.dto.DetalleVentaDTO;
import com.jose.sicov.util.IMapper;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_ventas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetalleVenta extends Base implements IMapper<DetalleVentaDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote; 

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitarioVenta;

    @Override
    public void setData(DetalleVentaDTO dto) {
        throw new UnsupportedOperationException("setData not implemented for DetalleVenta");
    }

    @Override
    public DetalleVentaDTO getDto() {
        return DetalleVentaDTO.builder()
            .productoId(this.producto != null ? this.producto.getId() : null)
            .loteId(this.lote != null ? this.lote.getId() : null)
            .cantidad(this.cantidad)
            .precioUnitarioVenta(this.precioUnitarioVenta)
            .build();
    }
}