package com.jose.sicov.filter;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * DTO utilizado para recibir los parámetros de consulta
 * que permiten filtrar la lista de ventas (e.g., por fechas,
 * nombre de cliente o rango de totales).
 */
@Data
public class VentaFilter {
    private String clienteNombre; 
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private BigDecimal totalMinimo;
    private BigDecimal totalMaximo;
}
