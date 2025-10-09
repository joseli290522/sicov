package com.jose.sicov.model;

import com.jose.sicov.dto.ClienteDTO;
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
@Table(name = "clientes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cliente extends Base implements IMapper<ClienteDTO> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String contacto;

    @Column(length = 150)
    private String correo;

    @Column(length = 200)
    private String direccion;

    @Override
    public ClienteDTO getDto() {
        return ClienteDTO.builder()
            .id(this.id)
            .nombre(this.nombre)
            .contacto(this.contacto)
            .correo(this.correo)
            .direccion(this.direccion)
            .activo(this.activo)
            .eliminado(this.eliminado)
            .creadoEn(this.creadoEn)
            .actualizadoEn(this.actualizadoEn)
            .build();
    }

    @Override
    public void setData(ClienteDTO clienteDTO) {
        this.nombre = clienteDTO.getNombre();
        this.contacto = clienteDTO.getContacto();
        this.correo = clienteDTO.getCorreo();
        this.direccion = clienteDTO.getDireccion();
        this.activo = clienteDTO.getActivo() != null ? clienteDTO.getActivo() : true;
        this.eliminado = clienteDTO.getEliminado() != null ? clienteDTO.getEliminado() : false;
    }
    
}
