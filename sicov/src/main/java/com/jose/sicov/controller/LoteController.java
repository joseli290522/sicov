package com.jose.sicov.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.dto.LoteEntradaDTO;
import com.jose.sicov.service.impl.LoteServiceImpl;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    @Autowired private LoteServiceImpl loteService; 

    @PostMapping("/entrada")
    public ResponseEntity<LoteDTO> registrarEntrada(@RequestBody LoteEntradaDTO loteEntradaDTO) {
        LoteDTO loteDTO = loteService.registrarEntrada(loteEntradaDTO);
        return new ResponseEntity<>(loteDTO, HttpStatus.CREATED);
    }
    
    // --- ENDPOINT 2: Búsqueda Avanzada con Paginación (GET) ---
    // Recibe los filtros como RequestParam y la paginación a través de Pageable.
    @GetMapping
    public ResponseEntity<Page<LoteDTO>> buscarLotesFiltrados(
        @RequestParam(required = false) Long productoId,
        @RequestParam(required = false) Long almacenId,
        @RequestParam(required = false) String numeroLote,
        @RequestParam(required = false) Boolean stockDisponible,
        @RequestParam(required = false) Boolean vencido,
        // Spring construye Pageable automáticamente a partir de los parámetros URL (?page=0&size=10&sort=campo,asc)
        Pageable pageable 
    ) {
        Page<LoteDTO> lotePage = loteService.buscarLotesPaginados(
            productoId, 
            almacenId, 
            numeroLote, 
            stockDisponible, 
            vencido, 
            pageable
        );
        
        return ResponseEntity.ok(lotePage);
    }
}