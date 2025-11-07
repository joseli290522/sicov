package com.jose.sicov.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.jose.sicov.dto.ProveedorDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Proveedor;
import com.jose.sicov.repository.ProveedorRepository;
import com.jose.sicov.specification.ProveedorSpecification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProveedorServiceImpl {

    private ProveedorRepository proveedorRepository;

    public Page<ProveedorDTO> listar(String nombre, Boolean activo, Pageable pageable) {
        Specification<Proveedor> spec = ProveedorSpecification.filtrar(nombre, activo);
        return proveedorRepository.findAll(spec, pageable).map(Proveedor::getDto);
    }

    public ProveedorDTO guardar(ProveedorDTO dto) {
        if (proveedorRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un almacén con ese nombre");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setData(dto);
        return proveedorRepository.save(proveedor).getDto();
    }

    public ProveedorDTO actualizar(ProveedorDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(dto.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proveedor con el ID " + dto.getId() + " no encontrado"));

        if (!proveedor.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                proveedorRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese nombre");
        }

        proveedor.setData(dto);
        return proveedorRepository.save(proveedor).getDto();
    }

    public ProveedorDTO eliminar(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Proveedor con el ID " + id + " no encontrado"));
        
        proveedor.setEliminado(true);
        proveedor.setActivo(false);
        return proveedorRepository.save(proveedor).getDto();
    }

}
