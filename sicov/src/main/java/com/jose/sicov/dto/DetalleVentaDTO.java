package com.jose.sicov.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {

    private Long productoId;
    private Long loteId; 
    private Integer cantidad;
    private BigDecimal precioUnitarioVenta;
}