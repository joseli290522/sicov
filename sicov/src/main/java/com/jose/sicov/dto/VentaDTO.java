package com.jose.sicov.dto;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class VentaDTO {
    
    private Long clienteId;    
    private Long almacenId;

    // Campos Financieros
    private BigDecimal subtotal;
    private BigDecimal ivaPorcentaje; 
    private BigDecimal iepsPorcentaje;
    private BigDecimal descuentoMonto;
    
    private BigDecimal totalFinal;

    // Campos de Pago
    private String metodoPago; 
    private BigDecimal montoRecibido; 
    
    @Builder.Default
    private LocalDate fechaVenta = LocalDate.now();

    private List<DetalleVentaDTO> detalles;
}