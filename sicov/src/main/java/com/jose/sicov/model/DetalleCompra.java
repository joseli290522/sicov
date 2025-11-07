package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import com.jose.sicov.dto.DetalleCompraDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "detalles_compra")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class DetalleCompra extends Base implements IMapper<DetalleCompraDTO> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Override
    public DetalleCompraDTO getDto() {
        return DetalleCompraDTO.builder()
            .productoNombre(this.producto.getNombre())
            .cantidad(this.cantidad)
            .costoUnitario(this.costoUnitario)
            .subtotalDetalle(this.costoUnitario.multiply(new BigDecimal(this.cantidad)))
            .build();
    }

    @Override
    public void setData(DetalleCompraDTO dto) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
    
}