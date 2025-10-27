package com.jose.sicov.controller;

import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.filter.VentaFilter;
import com.jose.sicov.service.impl.VentaServiceImpl;

@RestController
@RequestMapping("/api/ventas")
@AllArgsConstructor
public class VentaController {

    private final VentaServiceImpl ventaService;

    /**
     * POST /api/ventas: Registra una nueva venta.
     */
    @PostMapping
    public ResponseEntity<VentaDTO> crearVenta(@RequestBody VentaDTO ventaDTO) {
        VentaDTO nuevaVenta = ventaService.registrarNuevaVenta(ventaDTO).getDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    /**
     * GET /api/ventas: Lista ventas con paginación y filtros.
     * * Implementación usando @RequestParam para recibir filtros individuales.
     * Ejemplo de URL: /api/ventas?clienteNombre=Juan&fechaDesde=2023-01-01
     */
    @GetMapping
    public ResponseEntity<Page<VentaDTO>> listarVentas(
            @RequestParam(required = false) String clienteNombre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(required = false) BigDecimal totalMinimo,
            @RequestParam(required = false) BigDecimal totalMaximo,

            // Argumento para la paginación y ordenamiento
            Pageable pageable) {
        // 1. Construir el objeto VentaFilter a partir de los RequestParams
        VentaFilter filter = new VentaFilter();
        filter.setClienteNombre(clienteNombre);
        filter.setFechaDesde(fechaDesde);
        filter.setFechaHasta(fechaHasta);
        filter.setTotalMinimo(totalMinimo);
        filter.setTotalMaximo(totalMaximo);

        // 2. Usar el filtro y la paginación para la búsqueda
        return ResponseEntity.ok(ventaService.listarVentas(filter, pageable));
    }
}