package com.jose.sicov.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.util.IMapper;

@Entity
@Table(name = "lotes")
@Getter @Setter
public class Lote extends Base implements IMapper<LoteDTO>  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    @Column(name = "numero_lote", length = 50, nullable = false)
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    // EL ESTADO CRÍTICO DEL STOCK
    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual;

    @Override
    public LoteDTO getDto() {
        return LoteDTO.builder()
            .id(this.id)
            .productoId(this.producto.getId())
            .productoNombre(this.producto.getNombre())
            .almacenId(this.almacen.getId())
            .almacenNombre(this.almacen.getNombre())
            .numeroLote(this.numeroLote)
            .fechaVencimiento(this.fechaVencimiento)
            .cantidadInicial(this.cantidadInicial)
            .cantidadActual(this.cantidadActual)
            .build();
    }

    @Override
    public void setData(LoteDTO t) {
        throw new UnsupportedOperationException("Unimplemented method 'setData'");
    }
    
}