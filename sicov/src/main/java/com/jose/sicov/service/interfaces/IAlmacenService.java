package com.jose.sicov.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jose.sicov.dto.AlmacenDTO;

public interface IAlmacenService {

    Page<AlmacenDTO> listar(String nombre, String ubicacion, Boolean activo, Pageable pageable);
    AlmacenDTO guardar(AlmacenDTO dto);
    AlmacenDTO actualizar(AlmacenDTO dto);
    AlmacenDTO eliminar(Long id);
    AlmacenDTO cambiarEstado(Long id, boolean activo);
}
