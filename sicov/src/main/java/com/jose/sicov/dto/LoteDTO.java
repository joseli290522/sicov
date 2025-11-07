package com.jose.sicov.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoteDTO {

    private Long id;

    private Long productoId;
    private String productoNombre;

    private Long almacenId;
    private String almacenNombre;

    private String numeroLote;

    private LocalDate fechaVencimiento;
    private Integer cantidadInicial;
    private Integer cantidadActual;
    
}
