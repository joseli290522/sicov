package com.jose.sicov.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jose.sicov.model.Impuesto;
import com.jose.sicov.service.impl.ImpuestoServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/impuestos")
@AllArgsConstructor
public class ImpuestoController {

    private ImpuestoServiceImpl impuestoService;

    @GetMapping
    public ResponseEntity<List<Impuesto>> listar() {
        return ResponseEntity.ok(impuestoService.listar());
    }

      
    @GetMapping("/iva")
    public ResponseEntity<List<Impuesto>> listarIvaImpuestos() {
        return ResponseEntity.ok(impuestoService.listarIepsImpuestos());
    }

    @GetMapping("/ieps")
    public ResponseEntity<List<Impuesto>> listarIepsImpuestos() {
        return ResponseEntity.ok(impuestoService.listarIepsImpuestos());
    }

}
