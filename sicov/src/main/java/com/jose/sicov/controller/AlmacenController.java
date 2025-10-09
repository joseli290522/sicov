package com.jose.sicov.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jose.sicov.dto.AlmacenDTO;
import com.jose.sicov.service.impl.AlmacenServiceImpl;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/almacenes")
@AllArgsConstructor
public class AlmacenController {

    private AlmacenServiceImpl almacenService;

    @GetMapping
    public ResponseEntity<Page<AlmacenDTO>> listar(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String ubicacion,
        @RequestParam(required = false) Boolean activo,
        @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(almacenService.listar(nombre, ubicacion, activo, pageable));
    }

    @PostMapping
    public ResponseEntity<AlmacenDTO> crear(@RequestBody AlmacenDTO dto) {
        AlmacenDTO nuevo = almacenService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping
    public ResponseEntity<AlmacenDTO> actualizar(@RequestBody AlmacenDTO dto) {
        AlmacenDTO actualizado = almacenService.actualizar(dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AlmacenDTO> eliminar(@PathVariable Long id) {
        AlmacenDTO eliminado = almacenService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlmacenDTO> cambiarEstado(@PathVariable Long id, @RequestBody boolean activo) {
        AlmacenDTO actualizado = almacenService.cambiarEstado(id, activo);
        return ResponseEntity.ok(actualizado);
    }
    
}
