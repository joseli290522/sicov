package com.jose.sicov.dto;


import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class VentaDTO {

    private Long id;
    
    private Long clienteId;
    private String clienteNombre;

    private Long almacenId;
    private String almacenNombre;

    // Campos Financieros
    private BigDecimal subtotal;

    private Long impuestoIvaId;
    private BigDecimal ivaPorcentaje; 

    private Long impuestoIepsId;
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