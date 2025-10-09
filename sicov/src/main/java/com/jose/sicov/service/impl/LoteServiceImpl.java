package com.jose.sicov.service.impl;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.dto.LoteEntradaDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Almacen;
import com.jose.sicov.model.LoteEntrada;
import com.jose.sicov.model.Lote;
import com.jose.sicov.model.Producto;
import com.jose.sicov.repository.AlmacenRepository;
import com.jose.sicov.repository.LoteEntradaRepository;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.repository.ProductoRepository;
import com.jose.sicov.service.interfaces.ILoteService;
import com.jose.sicov.specification.LoteSpecification;

@Service
public class LoteServiceImpl implements ILoteService {
    
    @Autowired private LoteRepository loteRepository;
    @Autowired private LoteEntradaRepository loteEntradaRepository;
    @Autowired private ProductoRepository productoRepository; 
    @Autowired private AlmacenRepository almacenRepository;   

    /**
     * Registra un movimiento de entrada de stock, actualizando el lote y creando el registro histórico.
     * Recibe LoteEntradaDTO y devuelve LoteDTO.
     */
    @Override
    @Transactional 
    public LoteDTO registrarEntrada(LoteEntradaDTO dto) { 
        
        // 1. Validar entidades relacionadas
        Producto producto = productoRepository.findById(dto.getProductoId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + dto.getProductoId()));
        
        Almacen almacen = almacenRepository.findById(dto.getAlmacenId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado con ID: " + dto.getAlmacenId()));

        // 2. Buscar o crear Lote
        Optional<Lote> loteExistente = loteRepository
            .findByProductoIdAndAlmacenIdAndNumeroLoteAndEliminadoFalse(
                dto.getProductoId(), dto.getAlmacenId(), dto.getNumeroLote());
        
        Lote lote = loteExistente.orElseGet(() -> {
            Lote nuevo = new Lote();
            nuevo.setProducto(producto); 
            nuevo.setAlmacen(almacen);   
            nuevo.setNumeroLote(dto.getNumeroLote());
            nuevo.setFechaVencimiento(dto.getFechaVencimiento());
            return nuevo;
        });
        
        // 3. Actualizar stock del Lote
        lote.setCantidadActual(lote.getCantidadActual() + dto.getCantidad());
        Lote loteGuardado = loteRepository.save(lote);

        // 4. Registrar movimiento histórico (LoteEntrada)
        LoteEntrada entrada = new LoteEntrada();
        entrada.setLote(loteGuardado); 
        entrada.setCantidadEntrada(dto.getCantidad());
        entrada.setReferenciaEntrada(dto.getReferenciaEntrada()); 
        loteEntradaRepository.save(entrada); 

        // 5. Mapear y retornar DTO de detalle (LoteDTO)
        return loteGuardado.getDto(); 
    }

    /**
     * Busca lotes aplicando filtros dinámicos (Specification) y paginación (Pageable).
     * Devuelve un objeto Page de LoteDTO.
     */
    @Override
    @Transactional(readOnly = true) 
    public Page<LoteDTO> buscarLotesPaginados(
        Long productoId, 
        Long almacenId, 
        String numeroLote, 
        Boolean stockDisponible, 
        Boolean vencido,
        Pageable pageable
    ) {
        // 1. Construir la especificación a partir de los parámetros
        Specification<Lote> spec = LoteSpecification.filtrar(
            productoId, 
            almacenId, 
            numeroLote, 
            stockDisponible, 
            vencido
        );
        
        // 2. Ejecutar la consulta paginada
        Page<Lote> lotePage = loteRepository.findAll(spec, pageable);
        
        // 3. Mapear la página de entidades a una página de DTOs usando Lote::getDto
        return lotePage.map(Lote::getDto);
    }
}