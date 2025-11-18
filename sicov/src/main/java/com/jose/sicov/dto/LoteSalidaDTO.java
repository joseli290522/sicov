package com.jose.sicov.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoteSalidaDTO {
    
    private Long id;

    private LoteDTO lote;

    private DetalleVentaDTO detalle;

    private Integer cantidadConsumida;

    private LocalDateTime fechaSalida;
}
