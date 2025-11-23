package com.jose.sicov.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.jose.sicov.dto.DetalleVentaDTO;
import com.jose.sicov.dto.LoteDTO;
import com.jose.sicov.dto.LoteSalidaDTO;
import com.jose.sicov.dto.VentaDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.*;
import com.jose.sicov.repository.LoteRepository;
import com.jose.sicov.repository.VentaRepository;

import jakarta.servlet.http.HttpServletResponse;

import com.jose.sicov.repository.AlmacenRepository;
import com.jose.sicov.repository.ClienteRepository;
import com.jose.sicov.repository.ImpuestoRepository;
import com.jose.sicov.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter; 

// IMPORTS NECESARIOS PARA IMAGEN
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;


@Service
public class VentaServiceImpl {

    @Autowired private VentaRepository ventaRepository; 
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private AlmacenRepository almacenRepository; 
    @Autowired private ProductoRepository productoRepository; 
    @Autowired private LoteRepository loteRepository;
    @Autowired private ImpuestoRepository impuestoRepository;

    // Constantes para operaciones de BigDecimal
    private static final BigDecimal CIEN = new BigDecimal("100.0");
    private static final int DECIMAL_SCALE = 2; // Dos decimales para moneda

    // Ruta del logo en la carpeta 'resources/static/'
    private static final String LOGO_PATH = "static/logo-angroinsumos-servin.jpeg";

    @Transactional
    public Venta registrarNuevaVenta(VentaDTO ventaDTO) {

        // 1. Validar Cliente
        Cliente cliente = clienteRepository.findById(ventaDTO.getClienteId())
            .orElseThrow(() -> new NoSuchElementException("Cliente no encontrado con ID: " + ventaDTO.getClienteId()));
        
        Almacen almacen = almacenRepository.findById(ventaDTO.getAlmacenId())
            .orElseThrow(() -> new NoSuchElementException("Almacén no encontrado con ID: " + ventaDTO.getAlmacenId()));
        
        Impuesto iva = impuestoRepository.findById(ventaDTO.getImpuestoIvaId())
            .orElseThrow(() -> new NoSuchElementException("IVA no encontrado"));
        
        

        // 2. Crear la cabecera
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setAlmacen(almacen);
        venta.setImpuestoIVA(iva);

        if (ventaDTO.getImpuestoIepsId() != null) {
            venta.setImpuestoIEPS(impuestoRepository.findById(ventaDTO.getImpuestoIepsId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("IEPS no encontrado")));
        }

        //venta.setImpuestoIEPS(ieps);
        venta.setSubtotal(ventaDTO.getSubtotal());
        venta.setTotalFinal(ventaDTO.getTotalFinal()); 
        //venta.setFechaVenta(ventaDTO.getFechaVenta());
        venta.setFechaVenta(LocalDate.now(ZoneId.of("America/Mexico_City")));
        venta.setMetodoPago(ventaDTO.getMetodoPago());
        venta.setMontoRecibido(ventaDTO.getMontoRecibido());
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

    // ====================================================================================
    // MÉTODOS DE EXPORTACIÓN A PDF (Mejorados con Logo)
    // ====================================================================================
    
    public void exportarVentaAPdf(Long ventaId, HttpServletResponse response) {

        // 1. Inicialización de formatos
        Locale esMX = new Locale.Builder().setLanguage("es").setRegion("MX").build();
        DecimalFormat currencyFormatter = new DecimalFormat("¤#,##0.00", new java.text.DecimalFormatSymbols(esMX));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", esMX);
        
        try {
            // Obtener DTO de la venta (MANTENIDO)
            VentaDTO venta = ventaRepository.findById(ventaId)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con ID: " + ventaId))
                                .getDto(); 
            
            // 2. Configuración de la respuesta HTTP y el documento PDF
            response.setContentType("application/pdf");
            String fileName = "nota_venta_F" + venta.getId() + ".pdf";
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 30, 40, 30); 

            // --- 3. INSERCIÓN DEL LOGOTIPO ---
            try {
                // ClassPathResource ayuda a encontrar archivos dentro de src/main/resources
                ClassPathResource resource = new ClassPathResource(LOGO_PATH);
                ImageData data = ImageDataFactory.create(FileCopyUtils.copyToByteArray(resource.getInputStream()));
                
                Image logo = new Image(data);
                // Establecer un ancho fijo o proporcional (ej. 80 puntos de ancho)
                logo.setWidth(UnitValue.createPointValue(80));
                logo.setHeight(UnitValue.createPointValue(80));
                // Centrar el logo
                logo.setTextAlignment(TextAlignment.CENTER);
                
                document.add(logo);
            } catch (IOException e) {
                // Si el logo no se encuentra o hay error de lectura, solo se ignora y se loggea (no detiene la generación del PDF)
                System.err.println("Advertencia: No se pudo cargar el logo desde la ruta " + LOGO_PATH);
                e.printStackTrace();
            }


            // --- 4. ENCABEZADO DE LA EMPRESA (Información Profesional) ---
            document.add(new Paragraph("AGROSERVIN S.A. DE C.V.")
                    .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER).setMarginTop(5)); // Ajuste de margen tras posible logo
            document.add(new Paragraph("RFC: ASCV990101XYZ | Dirección: Calle Principal #123, CP 58000")
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Teléfono: (443) 123-4567 | E-mail: contacto@agroservin.com")
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER).setMarginBottom(10));
            
            // Título de la Nota
            document.add(new Paragraph("NOTA DE VENTA")
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(0));

            document.add(new Paragraph("FOLIO: #" + venta.getId())
                    .setFontSize(14).setBold().setTextAlignment(TextAlignment.CENTER).setMarginBottom(15));
            
            // --- 5. INFORMACIÓN DEL CLIENTE/TRANSACCIÓN (Mejorada) ---
            Table infoTable = new Table(UnitValue.createPercentArray(new float[] { 25, 75 }))
                    .useAllAvailableWidth().setFontSize(10).setMarginBottom(15).setBorder(null);

            // Cliente 
            String cliente = Optional.ofNullable(venta.getClienteNombre()).orElse("PÚBLICO GENERAL");
            infoTable.addCell(createLabelCell("Cliente:"));
            infoTable.addCell(createValueCell(cliente));

            // Fecha
            infoTable.addCell(createLabelCell("Fecha:"));
            infoTable.addCell(createValueCell(venta.getFechaVenta().format(dateFormatter)));

            infoTable.addCell(createLabelCell("Pago:"));
            infoTable.addCell(createValueCell(venta.getMetodoPago()));
            
            infoTable.addCell(createLabelCell("Almacén:"));
            infoTable.addCell(createValueCell(venta.getAlmacenNombre()));

            document.add(infoTable);

            document.add(new Paragraph("Detalles de Productos")
                    .setBold().setFontSize(12).setMarginTop(10).setMarginBottom(5));

            // --- 6. TABLA DE DETALLES DE PRODUCTOS ---
            Table detailsTable = new Table(UnitValue.createPercentArray(new float[] { 5, 45, 10, 20, 20 }))
                    .useAllAvailableWidth().setFontSize(9).setMarginBottom(20);

            String[] detailHeaders = { "#", "Producto / Lote(s)", "Cant.", "Precio Unitario", "Subtotal" };
            for (String header : detailHeaders) {
                detailsTable.addHeaderCell(createHeaderCell(header));
            }

            List<DetalleVentaDTO> detalles = Optional.ofNullable(venta.getDetalles()).orElseGet(List::of);

            int index = 1;
            for (DetalleVentaDTO detalle : detalles) {
                // Columna #
                detailsTable.addCell(createCell(String.valueOf(index++), false, TextAlignment.CENTER)); 
                // Columna Producto / Lote(s)
                detailsTable.addCell(buildLotesCell(detalle));
                // Columna Cantidad
                detailsTable.addCell(createCell(String.valueOf(detalle.getCantidad()), false, TextAlignment.CENTER)); 
                // Columna Precio Unitario
                detailsTable.addCell(createCell(currencyFormatter.format(detalle.getPrecioUnitarioVenta()), false, TextAlignment.RIGHT)); 
                // Columna Subtotal
                detailsTable.addCell(createCell(currencyFormatter.format(detalle.getSubtotalDetalle()), true, TextAlignment.RIGHT)); 
            }
            document.add(detailsTable);

            // --- 7. TABLA DE TOTALES Y PAGOS (Cálculos robustos) ---
            Table totalTable = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
                    .useAllAvailableWidth().setFontSize(10).setTextAlignment(TextAlignment.RIGHT);
            
            // Subtotal
            BigDecimal base = venta.getSubtotal(); 
            totalTable.addCell(createTotalRowLabel("Subtotal:"));
            totalTable.addCell(createTotalRowValue(currencyFormatter.format(base), false));

            // IVA
            BigDecimal ivaPorcentaje = venta.getIvaPorcentaje(); 
            BigDecimal iva = base.multiply(ivaPorcentaje)
                                 .divide(CIEN, DECIMAL_SCALE, RoundingMode.HALF_UP); 
            
            totalTable.addCell(createTotalRowLabel("IVA (" + ivaPorcentaje.intValue() + "%):"));
            totalTable.addCell(createTotalRowValue(currencyFormatter.format(iva), false));

            // IEPS (si aplica)
            BigDecimal iepsPorcentaje = venta.getIepsPorcentaje();
            if (iepsPorcentaje != null && iepsPorcentaje.compareTo(BigDecimal.ZERO) > 0) {
                 BigDecimal ieps = base.multiply(iepsPorcentaje)
                                       .divide(CIEN, DECIMAL_SCALE, RoundingMode.HALF_UP);
                 
                 totalTable.addCell(createTotalRowLabel("IEPS (" + iepsPorcentaje.intValue() + "%):"));
                 totalTable.addCell(createTotalRowValue(currencyFormatter.format(ieps), false)); 
            }

            // TOTAL FINAL
            BigDecimal totalFinal = venta.getTotalFinal();
            totalTable.addCell(createTotalRowLabel("MONTO TOTAL:")); 
            totalTable.addCell(createTotalRowValue(currencyFormatter.format(totalFinal), true)); 

            // MONTO RECIBIDO Y CAMBIO
            BigDecimal montoRecibido = venta.getMontoRecibido() != null ? venta.getMontoRecibido() : BigDecimal.ZERO; 
            BigDecimal cambio = montoRecibido.subtract(totalFinal); 

            totalTable.addCell(createTotalRowLabel("Monto Recibido:"));
            totalTable.addCell(createTotalRowValue(currencyFormatter.format(montoRecibido), false));
            
            totalTable.addCell(createTotalRowLabel("Cambio:")); 
            totalTable.addCell(createTotalRowValue(currencyFormatter.format(cambio), true)); 

            document.add(totalTable);
            
            // --- 8. NOTAS FINALES (Pie de página) ---
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("NOTA: Los precios incluyen impuestos. Conserve este documento para cualquier aclaración.")
                    .setFontSize(8).setItalic().setTextAlignment(TextAlignment.CENTER));

            document.showTextAligned(
                    new Paragraph("SICOV - Sistema de Control de Ventas e Inventario © Agroservin 2025")
                            .setFontSize(8),
                    297, 20, pdf.getNumberOfPages(),
                    TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);

            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF de la venta", e);
        } catch (RecursoNoEncontradoException e) {
             throw e; // Lanza la excepción si la venta no existe
        } catch (Exception e) {
             throw new RuntimeException("Error al procesar los datos de la venta: " + e.getMessage(), e);
        }
    }
    
    // ====================================================================================
    // MÉTODOS AUXILIARES DE ESTILO PARA PDF
    // ====================================================================================

    private Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content).setBold().setFontColor(ColorConstants.WHITE).setFontSize(9))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
    }
    
    private Cell createCell(String content, boolean bold, TextAlignment alignment) {
         Paragraph p = new Paragraph(content).setFontSize(9);
         if (bold) p.setBold();
         return new Cell().add(p).setTextAlignment(alignment).setVerticalAlignment(VerticalAlignment.TOP);
    }

    private Cell createLabelCell(String content) {
        Paragraph p = new Paragraph(content).setFontSize(10).setBold();
        return new Cell().add(p).setBorder(null).setTextAlignment(TextAlignment.LEFT);
    }

    private Cell createValueCell(String content) {
        return new Cell().add(new Paragraph(content).setFontSize(10)).setBorder(null).setTextAlignment(TextAlignment.LEFT);
    }
    
    private Cell createTotalRowLabel(String content) {
        return new Cell().add(new Paragraph(content).setBold().setFontSize(10)).setBorder(null).setTextAlignment(TextAlignment.RIGHT);
    }

    private Cell createTotalRowValue(String content, boolean isFinal) {
        Cell cell = new Cell().add(new Paragraph(content).setBold().setFontSize(11)).setBorder(null).setTextAlignment(TextAlignment.RIGHT);
        if (isFinal) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }
        return cell;
    }
    
    /** * Construye la celda que lista el nombre del producto y los lotes consumidos. 
     */
    private Cell buildLotesCell(DetalleVentaDTO detalle) {
        Div div = new Div();
        
        // Nombre del Producto (en negrita)
        div.add(new Paragraph(detalle.getProductoNombre())
                .setFontSize(9).setBold().setMarginBottom(0));
        
        List<LoteSalidaDTO> lotes = Optional.ofNullable(detalle.getLotesConsumidos()).orElseGet(List::of);

        // Lista de Lotes consumidos
        if (!lotes.isEmpty()) {
            for (LoteSalidaDTO loteSalida : lotes) {
                LoteDTO loteDTO = Optional.ofNullable(loteSalida.getLote())
                    .orElse(LoteDTO.builder().numeroLote("N/A").build()); 
                
                String loteText = String.format("Lote: %s (Cant: %d)", 
                    loteDTO.getNumeroLote(), 
                    loteSalida.getCantidadConsumida());
                
                div.add(new Paragraph(loteText)
                    .setFontSize(8).setItalic().setMarginTop(0).setMarginBottom(0));
            }
        } else {
             div.add(new Paragraph("Sin lotes asignados")
                    .setFontSize(8).setItalic().setMarginTop(0).setMarginBottom(0));
        }

        return new Cell().add(div).setTextAlignment(TextAlignment.LEFT).setVerticalAlignment(VerticalAlignment.TOP);
    }
}