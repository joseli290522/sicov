package com.jose.sicov.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<List<Impuesto>> listar() {
        return ResponseEntity.ok(impuestoService.listar());
    }

}
