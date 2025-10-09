package com.jose.sicov.model;

import java.time.LocalDate;

import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.util.IMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lotes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lote extends Base implements IMapper<LoteDTO>  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto; 
    
    // Relación con Almacen
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen; 

    @Column(name = "numero_lote", nullable = false, length = 50)
    private String numeroLote;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_actual", nullable = false)
    private Integer cantidadActual = 0; // Valor inicial

    @Override
    public LoteDTO getDto() {
        return LoteDTO.builder()
            .id(this.id)
            .numeroLote(this.numeroLote)
            .fechaVencimiento(this.fechaVencimiento)
            .cantidadActual(this.cantidadActual)

            // Mapeo de relaciones
            .productoId(this.producto != null ? this.producto.getId() : null)
            .productoNombre(this.producto != null ? this.producto.getNombre() : null)
            .almacenId(this.almacen != null ? this.almacen.getId() : null)
            .almacenNombre(this.almacen != null ? this.almacen.getNombre() : null)
            .build();
    }

    @Override
    public void setData(LoteDTO dto) {
        this.numeroLote = dto.getNumeroLote();
        this.fechaVencimiento = dto.getFechaVencimiento();
    }

    
}
