package com.jose.sicov.model;

import com.jose.sicov.dto.ProveedorDTO;
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
@Table(name = "proveedores")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Proveedor extends Base implements IMapper<ProveedorDTO> {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Override
    public ProveedorDTO getDto() {
        return ProveedorDTO.builder()
            .id(this.id)   
            .nombre(this.nombre)
            .telefono(this.telefono)
            .email(this.email)
            .direccion(this.direccion)
            .activo(this.activo)
            .build();
    }

    @Override
    public void setData(ProveedorDTO dto) {
        this.nombre = dto.getNombre();
        this.telefono = dto.getTelefono();
        this.email = dto.getEmail();
        this.direccion = dto.getDireccion();
    }
}
