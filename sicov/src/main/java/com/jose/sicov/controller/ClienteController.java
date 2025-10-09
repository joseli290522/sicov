package com.jose.sicov.controller;


import com.jose.sicov.dto.ClienteDTO;
import com.jose.sicov.service.impl.ClienteServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@AllArgsConstructor
public class ClienteController {

    private final ClienteServiceImpl clienteService;

    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> listar(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) String contacto,
        @RequestParam(required = false) Boolean activo,
        @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(clienteService.listar(nombre, contacto, activo, pageable));
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO dto) {
        ClienteDTO nuevo = clienteService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping
    public ResponseEntity<ClienteDTO> actualizar(@RequestBody ClienteDTO dto) {
        ClienteDTO actualizado = clienteService.actualizar(dto);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ClienteDTO> eliminar(@PathVariable Long id) {
        ClienteDTO eliminado = clienteService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClienteDTO> cambiarEstado(@PathVariable Long id, @RequestBody boolean activo) {
        ClienteDTO actualizado = clienteService.cambiarEstado(id, activo);
        return ResponseEntity.ok(actualizado);
    }

    @PostMapping("/importar-excel")
    public ResponseEntity<List<ClienteDTO>> importarDesdeExcel(@RequestParam("file") MultipartFile file) {
        List<ClienteDTO> resultados = clienteService.importarClientesDesdeExcel(file);
        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/exportar-excel")
    public void exportarExcel(@RequestBody List<Long> ids, HttpServletResponse response) {
        clienteService.exportarClientesAExcel(ids, response);
    }

    @PostMapping("/exportar-pdf")
    public void exportarPdf(@RequestBody List<Long> ids, HttpServletResponse response) {
        clienteService.exportarClientesAPdf(ids, response);
    }
}
