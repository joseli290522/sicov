package com.jose.sicov.model;

import com.jose.sicov.dto.AlmacenDTO;
import com.jose.sicov.util.IMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "almacenes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Almacen extends Base implements IMapper<AlmacenDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String ubicacion;

    @Override
    public AlmacenDTO getDto() {
        return AlmacenDTO.builder()
            .id(this.id)
            .nombre(this.nombre)
            .ubicacion(this.ubicacion)
            .activo(this.activo)
            .eliminado(this.eliminado)
            .creadoEn(this.creadoEn)
            .actualizadoEn(this.actualizadoEn)
            .build(); 
    }

    @Override
    public void setData(AlmacenDTO almacenDTO) {
        this.nombre = almacenDTO.getNombre();
        this.ubicacion = almacenDTO.getUbicacion();
        this.activo = almacenDTO.getActivo() != null ? almacenDTO.getActivo() : true;
    }
    
}
