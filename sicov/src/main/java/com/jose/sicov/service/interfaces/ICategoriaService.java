package com.jose.sicov.service.interfaces;

import com.jose.sicov.dto.CategoriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoriaService {

    Page<CategoriaDTO> listar(String nombre, Boolean activo, Pageable pageable);
    CategoriaDTO guardar(CategoriaDTO dto);
    CategoriaDTO actualizar(CategoriaDTO dto);
    CategoriaDTO eliminar(Long id);
    CategoriaDTO cambiarEstado(Long id, boolean activo);
}
