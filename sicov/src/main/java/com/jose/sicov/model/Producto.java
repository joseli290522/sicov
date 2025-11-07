package com.jose.sicov.model;

import java.math.BigDecimal;
import com.jose.sicov.dto.ProductoDTO;
import com.jose.sicov.util.IMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto extends Base implements IMapper<ProductoDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "unidad_medida_id")
    private UnidadMedida unidadMedida;

    @ManyToOne(optional = false)
    @JoinColumn(name = "impuesto_iva_id")
    private Impuesto impuestoIVA;

    @ManyToOne
    @JoinColumn(name = "impuesto_ieps_id")
    private Impuesto impuestoIEPS;

    @Override
    public ProductoDTO getDto() {        
        return ProductoDTO.builder()
            .id(this.id)
            .nombre(this.nombre)
            .descripcion(this.descripcion)
            .precio(this.precio)

            .categoriaId(this.categoria != null ? this.categoria.getId() : null)
            .categoriaNombre(this.categoria != null ? this.categoria.getNombre() : null)

            .unidadMedidaId(this.unidadMedida != null ? this.unidadMedida.getId() : null)
            .unidadMedidaNombre(this.unidadMedida != null ? this.unidadMedida.getNombre() : null)

            .impuestoIvaId(this.impuestoIVA != null ? this.impuestoIVA.getId() : null)
            .ivaPorcentaje(this.impuestoIVA != null ? this.impuestoIVA.getPorcentaje() : BigDecimal.ZERO)

            .impuestoIepsId(this.impuestoIEPS != null ? this.impuestoIEPS.getId() : null)
            .iepsPorcentaje(this.impuestoIEPS != null ? this.impuestoIEPS.getPorcentaje() : BigDecimal.ZERO)

            .activo(this.activo)
            .eliminado(this.eliminado)
            .creadoEn(this.creadoEn)
            .actualizadoEn(this.actualizadoEn)
            .build();
    }

    @Override
    public void setData(ProductoDTO dto) {
        this.nombre = dto.getNombre();
        this.descripcion = dto.getDescripcion();
        this.precio = dto.getPrecio();
        this.activo = dto.getActivo();
        this.eliminado = dto.getEliminado() != null ? dto.getEliminado() : false;
    }    
}
