package com.jose.sicov.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Boolean eliminado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
