package com.jose.sicov.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder 
@AllArgsConstructor
@NoArgsConstructor
public class LoteDTO {
    
    private Long id;
    private Long productoId;
    private String productoNombre; 

    private Long almacenId;
    private String almacenNombre;  

    private String numeroLote;
    private LocalDate fechaVencimiento;
    private Integer cantidadActual; // El stock actual.
}
