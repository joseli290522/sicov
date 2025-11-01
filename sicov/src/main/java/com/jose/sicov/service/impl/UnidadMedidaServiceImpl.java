package com.jose.sicov.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jose.sicov.model.UnidadMedida;
import com.jose.sicov.repository.UnidadMedidaRepository;
import com.jose.sicov.service.interfaces.IUnidadService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UnidadMedidaServiceImpl implements IUnidadService {

    private UnidadMedidaRepository unidadMedidaRepository;

    public List<UnidadMedida> listar() {
        return unidadMedidaRepository.findAll();
    }
    
}
