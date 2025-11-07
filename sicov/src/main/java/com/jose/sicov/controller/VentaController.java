package com.jose.sicov.controller;

import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.model.Venta;
import com.jose.sicov.repository.VentaRepository;
import com.jose.sicov.service.impl.VentaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired private VentaServiceImpl ventaService;
    @Autowired private VentaRepository ventaRepository;

    // POST /api/ventas - UI: Botón "Confirmar Venta"
    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        Venta nuevaVenta = ventaService.registrarNuevaVenta(ventaDTO);
        return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
    }
    
    // GET /api/ventas - UI: Tabla de "Gestión de Ventas"
    @GetMapping
    public ResponseEntity<Page<VentaDTO>> listarVentas(
        @RequestParam(required = false) String query, // Búsqueda por Cliente/ID
        Pageable pageable) 
    {
        if (query != null && !query.trim().isEmpty()) {
            return ResponseEntity.ok(ventaRepository.searchVentas(query.trim(), pageable).map(Venta::getDto));
        } else {
            return ResponseEntity.ok(ventaRepository.findAll(pageable).map(Venta::getDto));
        }
    }
    
    // GET /api/ventas/{id} - UI: Detalle de Venta/Ticket
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        // NOTA: Para obtener los Lotes y Vencimientos en el ticket,
        // Eli debe asegurar que el frontend acceda a: Venta -> Detalles -> LotesConsumidos -> Lote
        Optional<Venta> venta = ventaRepository.findById(id); 
        return venta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}