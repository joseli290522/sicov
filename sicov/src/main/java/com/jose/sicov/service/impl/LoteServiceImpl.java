package com.jose.sicov.service.impl;

import com.jose.sicov.dto.DetalleCompraDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Almacen;
import com.jose.sicov.model.Lote;
import com.jose.sicov.model.Producto;
import com.jose.sicov.repository.AlmacenRepository;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.repository.ProductoRepository;
import com.jose.sicov.service.interfaces.ILoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LoteServiceImpl implements ILoteService {

    @Autowired private LoteRepository loteRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private AlmacenRepository almacenRepository;

    @Override
    @Transactional
    public Lote registrarEntrada(DetalleCompraDTO detalleDTO, Long almacenId) {
        
        // 1. Buscar Lote Existente (UPSERT)
        Optional<Lote> optionalLote = loteRepository.findByProductoIdAndAlmacenIdAndNumeroLoteAndActivoTrue(
            detalleDTO.getProductoId(), almacenId, detalleDTO.getNumeroLote());

        Lote lote;

        if (optionalLote.isPresent()) {
            // Caso 1: UPDATE (Suma la cantidad al lote existente)
            lote = optionalLote.get();
            lote.setCantidadActual(lote.getCantidadActual() + detalleDTO.getCantidad());
            lote.setCantidadInicial(lote.getCantidadInicial() + detalleDTO.getCantidad()); // También actualiza el inicial si se usa como histórico
            // Se puede actualizar la fecha de vencimiento si el DTO lo permite, pero mejor mantener el original.
        } else {
            // Caso 2: INSERT (Crea un nuevo lote)
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado."));

            Almacen almacen = almacenRepository.findById(almacenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Almacén no encontrado."));
            
            lote = new Lote();
            lote.setProducto(producto);
            lote.setAlmacen(almacen);
            lote.setNumeroLote(detalleDTO.getNumeroLote());
            lote.setFechaVencimiento(detalleDTO.getFechaVencimiento());
            lote.setCantidadInicial(detalleDTO.getCantidad());
            lote.setCantidadActual(detalleDTO.getCantidad());
            lote.setActivo(true);
        }

        return loteRepository.save(lote);
    }
}
