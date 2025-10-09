package com.jose.sicov.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ProductoDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    private Long categoriaId;
    private String categoriaNombre;

    private Long unidadMedidaId;
    private String unidadMedidaNombre;

    private Long impuestoIvaId;
    private BigDecimal ivaPorcentaje;

    private Long impuestoIepsId;
    private BigDecimal iepsPorcentaje;

    private Boolean activo;
    private Boolean eliminado;

    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
