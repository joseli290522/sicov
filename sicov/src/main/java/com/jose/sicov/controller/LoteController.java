package com.jose.sicov.controller;


import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.model.Lote;
import com.jose.sicov.repository.LoteRepository;
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
    public ResponseEntity<List<LoteDTO>> getLotesDisponibles() {
        // Usa el orden FEFO/FIFO
        List<Lote> lotes = loteRepository.findAllActiveLotesOrderedByProductAndExpiry();
        if (lotes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lotes.stream().map(Lote::getDto).collect(Collectors.toList()));
    }
}