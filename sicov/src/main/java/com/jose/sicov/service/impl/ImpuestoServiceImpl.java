package com.jose.sicov.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.jose.sicov.model.Impuesto;
import com.jose.sicov.repository.ImpuestoRepository;
import com.jose.sicov.service.interfaces.IImpuestoService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImpuestoServiceImpl implements IImpuestoService {

    private ImpuestoRepository impuestoRepository;

     public List<Impuesto> listar() {
        return impuestoRepository.findAll();
    }

    public List<Impuesto> listarIvaImpuestos() {
        return impuestoRepository.findByTipoAndActivoTrue("IVA");
    }

    public List<Impuesto> listarIepsImpuestos() {
        return impuestoRepository.findByTipoAndActivoTrue("IEPS");
    }
    
}
