package com.jose.sicov.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jose.sicov.model.MetodoPago;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) 
public class VentaDTO {
    
    private Long id;
    private String clienteNombre; 

    private Boolean activo;
    private Boolean eliminado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    private Long clienteId;
    private LocalDate fechaVenta; 
    private MetodoPago metodoPago;

    // Campos de totales
    private BigDecimal subtotal;
    private BigDecimal ivaPorcentaje;
    private BigDecimal iepsPorcentaje;
    private BigDecimal descuentoMonto;
    private BigDecimal totalFinal;
    private BigDecimal montoRecibido;

    private List<DetalleVentaDTO> detalles;
}