package com.jose.sicov.model;

import com.jose.sicov.dto.CategoriaDTO;
import com.jose.sicov.util.IMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Categoria extends Base implements IMapper<CategoriaDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Override
    public CategoriaDTO getDto() {
        return CategoriaDTO.builder()
            .id(this.id)
            .nombre(this.nombre)
            .descripcion(this.descripcion)
            .activo(this.activo)
            .eliminado(this.eliminado)
            .creadoEn(this.creadoEn)
            .actualizadoEn(this.actualizadoEn)
            .build(); 
        }

    @Override
    public void setData(CategoriaDTO categoriaDTO) {
        this.nombre = categoriaDTO.getNombre();
        this.descripcion = categoriaDTO.getDescripcion();
        this.activo = categoriaDTO.getActivo() != null ? categoriaDTO.getActivo() : true;
        this.eliminado = categoriaDTO.getEliminado() != null ? categoriaDTO.getEliminado() : false;
    }
}
