package com.jose.sicov.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jose.sicov.dto.ProveedorDTO;
import com.jose.sicov.service.impl.ProveedorServiceImpl;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/proveedores")
@AllArgsConstructor
public class ProveedorController {

    private ProveedorServiceImpl proveedorService;

    @GetMapping
    public ResponseEntity<Page<ProveedorDTO>> listar(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Boolean activo,
        @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(proveedorService.listar(nombre, activo, pageable));
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crear(@RequestBody ProveedorDTO dto) {
        ProveedorDTO nuevo = proveedorService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping
    public ResponseEntity<ProveedorDTO> actualizar(@RequestBody ProveedorDTO dto) {
        ProveedorDTO actualizado = proveedorService.actualizar(dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProveedorDTO> eliminar(@PathVariable Long id) {
        ProveedorDTO eliminado = proveedorService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }
    
}
