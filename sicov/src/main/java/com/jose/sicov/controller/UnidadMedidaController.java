package com.jose.sicov.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jose.sicov.model.UnidadMedida;
import com.jose.sicov.service.impl.UnidadMedidaServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/unidades-medida")
@AllArgsConstructor
public class UnidadMedidaController {

    private UnidadMedidaServiceImpl unidadMedidaService;

    @GetMapping
    public ResponseEntity<List<UnidadMedida>> listar() {
        return ResponseEntity.ok(unidadMedidaService.listar());
    }
}
