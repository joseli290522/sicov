package com.jose.sicov.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.dto.LoteEntradaDTO;

public interface ILoteService {
    
    LoteDTO registrarEntrada(LoteEntradaDTO dto); 
    
    // Método de búsqueda paginada, recibe los parámetros directamente
    Page<LoteDTO> buscarLotesPaginados(
        Long productoId, 
        Long almacenId, 
        String numeroLote, 
        Boolean stockDisponible, 
        Boolean vencido,
        Pageable pageable // Objeto de Paginación y Ordenamiento
    );
}
