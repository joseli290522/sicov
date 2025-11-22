package com.jose.sicov.controller;

import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.model.Venta;
import com.jose.sicov.repository.VentaRepository;
import com.jose.sicov.service.impl.VentaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1//ventas")
public class VentaController {

    @Autowired
    private VentaServiceImpl ventaService;
    @Autowired
    private VentaRepository ventaRepository;

    @PostMapping
    public ResponseEntity<VentaDTO> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        Venta nuevaVenta = ventaService.registrarNuevaVenta(ventaDTO);
        return new ResponseEntity<>(nuevaVenta.getDto(), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<VentaDTO>> listarVentas(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Pageable pageable) {
        if ((query != null && !query.trim().isEmpty()) || date != null) {

            String safeQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : "";

            return ResponseEntity.ok(
                    ventaRepository.searchVentas(safeQuery, date, pageable)
                            .map(Venta::getDto));
        } else {
            return ResponseEntity.ok(ventaRepository.findAll(pageable).map(Venta::getDto));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerVentaPorId(@PathVariable Long id) {
        Optional<Venta> venta = ventaRepository.findById(id);
        return ResponseEntity.ok(venta.get().getDto());
    }
}
