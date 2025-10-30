package com.jose.sicov.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCompraDTO {

    private Long productoId;
    private Integer cantidad;
    private BigDecimal costoUnitario;
    private String numeroLote;
    private LocalDate fechaVencimiento;
}