package com.jose.sicov.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleVentaDTO {

    private Long productoId;
    private Integer cantidad; 
    private BigDecimal precioUnitarioVenta;
    private BigDecimal subtotalDetalle;
}