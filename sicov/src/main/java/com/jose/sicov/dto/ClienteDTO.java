package com.jose.sicov.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    
    private Long id;
    private String nombre;
    private String contacto;
    private String correo;
    private String direccion;
    private Boolean activo;
    private Boolean eliminado;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}
