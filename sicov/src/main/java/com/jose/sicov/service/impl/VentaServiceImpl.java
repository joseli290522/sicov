package com.jose.sicov.service.impl;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jose.sicov.dto.DetalleVentaDTO;
import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.filter.VentaFilter;
import com.jose.sicov.model.Cliente;
import com.jose.sicov.model.DetalleVenta;
import com.jose.sicov.model.Lote;
import com.jose.sicov.model.Producto;
import com.jose.sicov.model.Venta;
import com.jose.sicov.repository.ClienteRepository;
import com.jose.sicov.repository.DetalleVentaRepository;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.repository.ProductoRepository;
import com.jose.sicov.repository.VentaRepository;
import com.jose.sicov.specification.VentaSpecification;

import jakarta.transaction.Transactional;

@Service
public class VentaServiceImpl {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private ClienteRepository clienteRepository; 
    @Autowired
    private ProductoRepository productoRepository; 
    @Autowired
    private LoteRepository loteRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    /**
     * Procesa, valida y registra una nueva venta, y descuenta el inventario.
     * @param ventaDTO DTO unificado que contiene los datos de la venta.
     * @return Entidad Venta guardada.
     */
    @Transactional
    public Venta registrarNuevaVenta(VentaDTO ventaDTO) {
        
        Cliente cliente = clienteRepository.findById(ventaDTO.getClienteId())
            .orElseThrow(() -> new NoSuchElementException("Cliente con ID " + ventaDTO.getClienteId() + " no encontrado."));
        
        Venta venta = new Venta();
        venta.setCliente(cliente); 
        venta.setData(ventaDTO);
        
        venta.setCreadoEn(LocalDateTime.now());
        venta.setActivo(true);
        venta.setEliminado(false);

        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Procesar cada detalle y descontar stock
        for (DetalleVentaDTO detalleDTO : ventaDTO.getDetalles()) {
            
            Producto producto = productoRepository.findById(detalleDTO.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto con ID " + detalleDTO.getProductoId() + " no encontrado."));
            
            Lote lote = loteRepository.findById(detalleDTO.getLoteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Lote con ID " + detalleDTO.getLoteId() + " no encontrado."));

            // 4. Crear y guardar DetalleVenta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            detalle.setLote(lote);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitarioVenta(detalleDTO.getPrecioUnitarioVenta());
            
            // Nota: Aquí se podría guardar el detalle si se usara DetalleVentaRepository,
            // pero si la lista 'detalles' en Venta está en CascadeType.ALL, se guarda automáticamente.
            // Para ser explícitos: detalleVentaRepository.save(detalle);
            detalleVentaRepository.save(detalle);


            // 5. Descontar inventario (CRÍTICO: con validación en la query)
            int rowsAffected = loteRepository.descontarInventario(detalleDTO.getLoteId(), detalleDTO.getCantidad());
            if (rowsAffected == 0) {
                 // Provoca Rollback de toda la transacción si el inventario fue insuficiente.
                 throw new IllegalStateException("El inventario para el lote " + detalleDTO.getLoteId() + " es insuficiente (" + detalleDTO.getCantidad() + " solicitado).");
            }
        }

        return ventaGuardada;
    }

    /**
     * Obtiene el listado de ventas con paginación y filtros.
     * @return Una página de VentaDTOs.
     */
    public Page<VentaDTO> listarVentas(VentaFilter filter, Pageable pageable) {
        Specification<Venta> spec = VentaSpecification.byFilter(filter);
        return ventaRepository.findAll(spec, pageable).map(Venta::getDto);
    }
}