package com.jose.sicov.service.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.jose.sicov.dto.ProductoDTO;

public interface IProductoService {

    Page<ProductoDTO> listar(String categoria, Boolean activo, String nombre, Pageable pageable);
    ProductoDTO guardar(ProductoDTO dto);
    ProductoDTO actualizar(ProductoDTO dto);
    ProductoDTO eliminar(Long id);
    ProductoDTO cambiarEstado(Long id, boolean activo);
    
}
