package com.jose.sicov.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LoteEntradaDTO {
    
    private Long productoId; // ID que se usa para buscar la entidad Producto
    private Long almacenId; // ID que se usa para buscar la entidad Almacen
    private String numeroLote;
    private Integer cantidad;
    private LocalDate fechaVencimiento;
    private String referenciaEntrada;
}