package com.jose.sicov.controller;


import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.model.Lote;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.service.impl.LoteServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/lotes")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository; 

    @Autowired
    private LoteServiceImpl loteService;

    // GET /api/lotes/producto/{productoId}/disponibles
    @GetMapping("/producto/{productoId}/disponibles")
    public ResponseEntity<List<LoteDTO>> getLotesDisponiblesPorProducto(@PathVariable Long productoId) {
        // Usa el orden FEFO/FIFO
        List<Lote> lotes = loteRepository.findLotesDisponiblesParaVenta(productoId);
        if (lotes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lotes.stream().map(Lote::getDto).collect(Collectors.toList()));
    }

   @GetMapping
    public ResponseEntity<List<LoteDTO>> getLotesDisponibles(
        @RequestParam(required = false) String query) { // 💡 Acepta el filtro 'query'
        
        // 1. Obtener la lista de Lotes, aplicando el filtro que viene del request
        List<Lote> lotes = loteService.getLotesActivosConFiltro(query);
        
        if (lotes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        // 2. Mapear a DTOs y devolver
        return ResponseEntity.ok(lotes.stream().map(Lote::getDto).collect(Collectors.toList()));
    }
}