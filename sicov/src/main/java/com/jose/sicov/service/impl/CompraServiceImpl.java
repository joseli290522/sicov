package com.jose.sicov.service.impl;


import com.jose.sicov.dto.CompraDTO;
import com.jose.sicov.dto.DetalleCompraDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.*;
import com.jose.sicov.repository.*; // Asumimos que existen
import com.jose.sicov.service.interfaces.ILoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.NoSuchElementException;

@Service
public class CompraServiceImpl {

    @Autowired private CompraRepository compraRepository; 
    @Autowired private DetalleCompraRepository detalleCompraRepository; 
    @Autowired private ProveedorRepository proveedorRepository; 
    @Autowired private AlmacenRepository almacenRepository; 
    @Autowired private ProductoRepository productoRepository; 
    @Autowired private ILoteService loteService; 

    /**
     * Registra una compra y crea/actualiza los lotes de stock.
     */
    @Transactional
    public Compra registrarCompra(CompraDTO compraDTO) {

        Proveedor proveedor = proveedorRepository.findById(compraDTO.getProveedorId())
            .orElseThrow(() -> new NoSuchElementException("Proveedor no encontrado."));

        Almacen almacen = almacenRepository.findById(compraDTO.getAlmacenId())
            .orElseThrow(() -> new NoSuchElementException("Almacén de destino no encontrado."));

        // 1. Mapear y Guardar Encabezado de Compra
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setAlmacen(almacen);
        compra.setTotal(compraDTO.getTotal()); 
        //compra.setFechaCompra(compraDTO.getFechaCompra());
        compra.setFechaCompra(LocalDate.now(ZoneId.of("America/Mexico_City")));
        compra.setActivo(true);
        Compra compraGuardada = compraRepository.save(compra);

        // 2. Procesar Detalles (La clave del Módulo)
        for (DetalleCompraDTO detalleDTO : compraDTO.getDetalles()) {

            // a. CREACIÓN/ACTUALIZACIÓN DE INVENTARIO (Llamada al LoteService)
            Lote loteActualizado = loteService.registrarEntrada(detalleDTO, almacen.getId());

            // b. REGISTRO HISTÓRICO
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado en el detalle de compra."));

            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompra(compraGuardada);
            detalle.setProducto(producto);
            detalle.setLote(loteActualizado); // Apunta al Lote que recibió el stock
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setCostoUnitario(detalleDTO.getCostoUnitario());
            detalle.setActivo(true);

            detalleCompraRepository.save(detalle);
        }

        return compraGuardada; // Retorna la entidad Compra
    }
}