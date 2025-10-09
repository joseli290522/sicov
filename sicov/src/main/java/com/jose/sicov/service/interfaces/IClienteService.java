package com.jose.sicov.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.jose.sicov.dto.ClienteDTO;

public interface IClienteService {
    
    Page<ClienteDTO> listar(String nombre, String contacto, Boolean activo, Pageable pageable);
    ClienteDTO guardar(ClienteDTO dto);
    ClienteDTO actualizar(ClienteDTO dto);
    ClienteDTO eliminar(Long id);
    ClienteDTO cambiarEstado(Long id, boolean activo);
}
