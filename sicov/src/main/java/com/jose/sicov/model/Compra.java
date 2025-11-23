package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.jose.sicov.dto.CompraDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "compras")
@Getter @Setter
public class Compra extends Base implements IMapper<CompraDTO> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen; // Almacén de destino
    
    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    @Override
    public CompraDTO getDto() {
        return CompraDTO.builder()
            .id(this.id)

            .proveedorId(this.proveedor.getId())
            .proveedorNombre(this.proveedor.getNombre())

            .almacenId(this.almacen.getId())
            .almacenNombre(this.almacen.getNombre())

            .fechaCompra(this.fechaCompra)
            
            .total(this.total)

            .detalles(this.detalles.stream().map(DetalleCompra::getDto).collect(Collectors.toList()))
            
            .build();
    }

    @Override
    public void setData(CompraDTO t) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
    
}