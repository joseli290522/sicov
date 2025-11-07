package com.jose.sicov.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.jose.sicov.dto.AlmacenDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Almacen;
import com.jose.sicov.repository.AlmacenRepository;
import com.jose.sicov.service.interfaces.IAlmacenService;
import com.jose.sicov.specification.AlmacenSpecification;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlmacenServiceImpl implements IAlmacenService {

    private AlmacenRepository almacenRepository;

    @Override
    public Page<AlmacenDTO> listar(String nombre, String ubicacion, Boolean activo, Pageable pageable) {
        Specification<Almacen> spec = AlmacenSpecification.filtrar(nombre, ubicacion, activo);
        return almacenRepository.findAll(spec, pageable).map(Almacen::getDto);
    }

    @Override
    public AlmacenDTO guardar(AlmacenDTO dto) {
        if (almacenRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un almacén con ese nombre");
        }

        Almacen almacen = new Almacen();
        almacen.setData(dto);
        return almacenRepository.save(almacen).getDto();
    }

    @Override
    public AlmacenDTO actualizar(AlmacenDTO dto) {
        Almacen almacen = almacenRepository.findById(dto.getId())
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Almacén con el ID " + dto.getId() + " no encontrado"));

        if (!almacen.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                almacenRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un almacén con ese nombre");
        }

        almacen.setData(dto);
        return almacenRepository.save(almacen).getDto();
    }

    @Override
    public AlmacenDTO eliminar(Long id) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén con el ID " + id + " no encontrado"));

        almacen.setEliminado(true);
        almacen.setActivo(false);
        return almacenRepository.save(almacen).getDto();
    }

    @Override
    public AlmacenDTO cambiarEstado(Long id, boolean activo) {
        Almacen almacen = almacenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén con el ID " + id + " no encontrado"));

        almacen.setActivo(activo);
        return almacenRepository.save(almacen).getDto();
    }

}
