package com.jose.sicov.controller;

import com.jose.sicov.dto.CompraDTO;
import com.jose.sicov.model.Compra;
import com.jose.sicov.repository.CompraRepository;
import com.jose.sicov.service.impl.CompraServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/compras")
public class CompraController {

    @Autowired private CompraServiceImpl compraService;
    @Autowired private CompraRepository compraRepository;

    // POST /api/compras - UI: Botón "Confirmar Compra"
    @PostMapping
    public ResponseEntity<CompraDTO> registrarCompra(@RequestBody CompraDTO compraDTO) {
        Compra nuevaCompra = compraService.registrarCompra(compraDTO);
        return new ResponseEntity<>(nuevaCompra.getDto(), HttpStatus.CREATED);
    }
    
    // GET /api/compras - UI: Lista de Historial de Compras
    @GetMapping
    public ResponseEntity<Page<CompraDTO>> listarCompras(Pageable pageable) {
        return ResponseEntity.ok(compraRepository.findAll(pageable).map(Compra::getDto));
    }
    
    // GET /api/compras/{id} - UI: Detalle de una compra
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> obtenerCompraPorId(@PathVariable Long id) {
        Optional<Compra> compra = compraRepository.findById(id);
        return ResponseEntity.ok(compra.get().getDto());
    }
}