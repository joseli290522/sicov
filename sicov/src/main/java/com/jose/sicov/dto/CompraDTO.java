package com.jose.sicov.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompraDTO {

    private Long id;

    private Long proveedorId;
    private String proveedorNombre;

    private Long almacenId;
    private String almacenNombre; 

    @Builder.Default
    private LocalDate fechaCompra = LocalDate.now(); 
    
    private BigDecimal total;
    
    private List<DetalleCompraDTO> detalles;
}