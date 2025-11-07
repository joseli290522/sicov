package com.jose.sicov.service.impl;

import com.jose.sicov.dto.DetalleVentaDTO;
import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.*;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.repository.VentaRepository;
import com.jose.sicov.repository.AlmacenRepository;
import com.jose.sicov.repository.ClienteRepository;
import com.jose.sicov.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VentaServiceImpl {

    @Autowired private VentaRepository ventaRepository; 
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private AlmacenRepository almacenRepository; 
    @Autowired private ProductoRepository productoRepository; 
    @Autowired private LoteRepository loteRepository;

    @Transactional
    public Venta registrarNuevaVenta(VentaDTO ventaDTO) {

        // 1. Validar Cliente
        Cliente cliente = clienteRepository.findById(ventaDTO.getClienteId())
            .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con ID: " + ventaDTO.getClienteId()));
        
        Almacen almacen = almacenRepository.findById(ventaDTO.getAlmacenId())
            .orElseThrow(() -> new NoSuchElementException("Almacén no encontrado con ID: " + ventaDTO.getAlmacenId()));

        // 2. Crear la cabecera
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setAlmacen(almacen);
        venta.setSubtotal(ventaDTO.getSubtotal());
        venta.setTotalFinal(ventaDTO.getTotalFinal()); 
        venta.setFechaVenta(ventaDTO.getFechaVenta());
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setActivo(true);
        
        List<DetalleVenta> detallesVenta = new ArrayList<>();

        // 3. Procesar cada detalle
        for (DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
            
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + detalleDTO.getProductoId()));

            // CRÍTICO: Lógica de consumo FEFO/FIFO que puede afectar MÚLTIPLES LOTES
            List<LoteSalida> lotesConsumidos = consumirLotesParaVenta(producto.getId(), ventaDTO.getAlmacenId(), detalleDTO.getCantidad());
            
            // Crear Detalle de Venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitarioVenta(detalleDTO.getPrecioUnitarioVenta());
            
            // Conectar la trazabilidad
            for (LoteSalida loteSalida : lotesConsumidos) {
                loteSalida.setDetalleVenta(detalle); 
            }
            detalle.setLotesConsumidos(lotesConsumidos);
            
            detallesVenta.add(detalle);
        }

        // 4. Asignar Detalles y Guardar
        venta.setDetalles(detallesVenta);
        return ventaRepository.save(venta);
    }
    
    /**
     * Implementa la lógica de descuento FEFO/FIFO de forma atómica.
     * Genera registros de LoteSalida por cada lote consumido.
     */
    private List<LoteSalida> consumirLotesParaVenta(Long productoId, Long almacenId, Integer cantidadRequerida) {
        
        // Trae lotes ordenados para consumo (FEFO)
        List<Lote> lotes = loteRepository.findLotesDisponiblesParaVenta(productoId);
        
        int restante = cantidadRequerida;
        List<LoteSalida> registrosSalida = new ArrayList<>();

        for (Lote lote : lotes) {
            if (restante <= 0) break;

            int disponible = lote.getCantidadActual();
            int aConsumir = Math.min(restante, disponible);

            if (aConsumir > 0) {
                // Descuento Atómico (Query UPDATE)
                int rowsAffected = loteRepository.descontarInventario(lote.getId(), aConsumir);
                
                if (rowsAffected == 0) {
                    throw new IllegalStateException("Fallo de concurrencia al descontar stock del Lote ID: " + lote.getId() + ".");
                }

                // Crear registro de trazabilidad
                LoteSalida registro = new LoteSalida();
                registro.setLote(lote);
                registro.setCantidadConsumida(aConsumir);
                registro.setActivo(true);
                
                registrosSalida.add(registro);
                restante -= aConsumir;
            }
        }

        if (restante > 0) {
            throw new IllegalStateException("Stock insuficiente para el Producto ID: " + productoId + ". Faltan " + restante + " unidades.");
        }
        
        return registrosSalida;
    }
}