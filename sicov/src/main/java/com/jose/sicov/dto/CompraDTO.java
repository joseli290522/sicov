package com.jose.sicov.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompraDTO {

    private Long proveedorId;
    private Long almacenId; 

    @Builder.Default
    private LocalDate fechaCompra = LocalDate.now(); 
    
    private BigDecimal total;
    private List<DetalleCompraDTO> detalles;
}