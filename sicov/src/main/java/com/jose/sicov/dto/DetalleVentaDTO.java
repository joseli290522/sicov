package com.jose.sicov.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleVentaDTO {

    private Long productoId;
    private String productoNombre;
    
    private Integer cantidad; 
    private BigDecimal precioUnitarioVenta;
    private BigDecimal subtotalDetalle;

    private List<LoteSalidaDTO> lotesConsumidos;
}