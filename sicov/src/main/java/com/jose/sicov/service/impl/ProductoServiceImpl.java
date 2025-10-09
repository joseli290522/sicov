package com.jose.sicov.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.jose.sicov.dto.ProductoDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Categoria;
import com.jose.sicov.model.Impuesto;
import com.jose.sicov.model.Producto;
import com.jose.sicov.model.UnidadMedida;
import com.jose.sicov.repository.CategoriaRepository;
import com.jose.sicov.repository.ImpuestoRepository;
import com.jose.sicov.repository.ProductoRepository;
import com.jose.sicov.repository.UnidadMedidaRepository;
import com.jose.sicov.service.interfaces.IProductoService;
import com.jose.sicov.specification.ProductoSpecification;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private ProductoRepository productoRepository;
    private CategoriaRepository categoriaRepository;
    private UnidadMedidaRepository unidadMedidaRepository;
    private ImpuestoRepository impuestoRepository;

    @Override
    public Page<ProductoDTO> listar(String categoria, Boolean activo, String nombre, Pageable pageable) {
        Specification<Producto> spec = ProductoSpecification.filtrar(categoria, activo, nombre);
        return productoRepository.findAll(spec, pageable).map(Producto::getDto);
    }

    @Override
    public ProductoDTO guardar(ProductoDTO dto) {
        if (productoRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        Producto producto = new Producto();
        producto.setData(dto);

        producto.setCategoria(categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada")));

        producto.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad de medida no encontrada")));

        producto.setImpuestoIVA(impuestoRepository.findById(dto.getImpuestoIvaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("IVA no encontrado")));

        if (dto.getImpuestoIepsId() != null) {
            producto.setImpuestoIEPS(impuestoRepository.findById(dto.getImpuestoIepsId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("IEPS no encontrado")));
        }

        return productoRepository.save(producto).getDto();
    }

    @Override
    public ProductoDTO actualizar(ProductoDTO dto) {
        Producto producto = productoRepository.findById(dto.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        if (!producto.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                productoRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
        }

        producto.setData(dto);

        producto.setCategoria(categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada")));

        producto.setUnidadMedida(unidadMedidaRepository.findById(dto.getUnidadMedidaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Unidad de medida no encontrada")));

        producto.setImpuestoIVA(impuestoRepository.findById(dto.getImpuestoIvaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("IVA no encontrado")));

        if (dto.getImpuestoIepsId() != null) {
            producto.setImpuestoIEPS(impuestoRepository.findById(dto.getImpuestoIepsId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("IEPS no encontrado")));
        } else {
            producto.setImpuestoIEPS(null);
        }

        return productoRepository.save(producto).getDto();
    }

    @Override
    public ProductoDTO eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        producto.setEliminado(true);
        producto.setActivo(false);
        return productoRepository.save(producto).getDto();
    }

    @Override
    public ProductoDTO cambiarEstado(Long id, boolean activo) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        producto.setActivo(activo);
        return productoRepository.save(producto).getDto();
    }

    public List<ProductoDTO> importarProductosDesdeExcel(MultipartFile file) {
        List<ProductoDTO> resultados = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    // Leer campos como texto
                    String nombreProducto = row.getCell(0).getStringCellValue().trim();
                    String descripcion = row.getCell(1).getStringCellValue().trim();

                    // Validar duplicado
                    if (productoRepository.findByNombre(nombreProducto).isPresent()) {
                        System.out.println("Producto duplicado en fila " + i + ": " + nombreProducto);
                        continue;
                    }

                    // Leer precio como numérico o texto
                    BigDecimal precio;
                    Cell precioCell = row.getCell(2);
                    if (precioCell.getCellType() == CellType.NUMERIC) {
                        precio = BigDecimal.valueOf(precioCell.getNumericCellValue());
                    } else if (precioCell.getCellType() == CellType.STRING) {
                        precio = new BigDecimal(precioCell.getStringCellValue().trim());
                    } else {
                        throw new IllegalArgumentException("Precio inválido en fila " + i);
                    }

                    // Leer nombres de entidades relacionadas
                    String nombreCategoria = row.getCell(3).getStringCellValue().trim();
                    String nombreUnidad = row.getCell(4).getStringCellValue().trim();
                    String nombreIVA = row.getCell(5).getStringCellValue().trim();
                    String nombreIEPS = row.getCell(6) != null ? row.getCell(6).getStringCellValue().trim() : null;

                    // Leer activo como booleano o texto
                    Boolean activo;
                    Cell activoCell = row.getCell(7);
                    if (activoCell.getCellType() == CellType.BOOLEAN) {
                        activo = activoCell.getBooleanCellValue();
                    } else if (activoCell.getCellType() == CellType.STRING) {
                        activo = Boolean.parseBoolean(activoCell.getStringCellValue().trim().toLowerCase());
                    } else {
                        throw new IllegalArgumentException("Activo inválido en fila " + i);
                    }

                    // Resolver entidades por nombre
                    Categoria categoria = categoriaRepository.findByNombre(nombreCategoria)
                            .orElseThrow(
                                    () -> new RecursoNoEncontradoException(
                                            "Categoría no encontrada: " + nombreCategoria));

                    UnidadMedida unidad = unidadMedidaRepository.findByNombre(nombreUnidad)
                            .orElseThrow(
                                    () -> new RecursoNoEncontradoException("Unidad no encontrada: " + nombreUnidad));

                    Impuesto iva = impuestoRepository.findByNombre(nombreIVA)
                            .orElseThrow(() -> new RecursoNoEncontradoException("IVA no encontrado: " + nombreIVA));

                    Impuesto ieps = (nombreIEPS != null && !nombreIEPS.isBlank())
                            ? impuestoRepository.findByNombre(nombreIEPS).orElse(null)
                            : null;

                    // Construir DTO completo
                    ProductoDTO dto = ProductoDTO.builder()
                            .nombre(nombreProducto)
                            .descripcion(descripcion)
                            .precio(precio)
                            .categoriaId(categoria.getId())
                            .categoriaNombre(categoria.getNombre())
                            .unidadMedidaId(unidad.getId())
                            .unidadMedidaNombre(unidad.getNombre())
                            .impuestoIvaId(iva.getId())
                            .ivaPorcentaje(iva.getPorcentaje())
                            .impuestoIepsId(ieps != null ? ieps.getId() : null)
                            .iepsPorcentaje(ieps != null ? ieps.getPorcentaje() : BigDecimal.ZERO)
                            .activo(activo)
                            .eliminado(false)
                            .build();

                    // Construir entidad y guardar
                    Producto producto = new Producto();
                    producto.setData(dto);
                    producto.setCategoria(categoria);
                    producto.setUnidadMedida(unidad);
                    producto.setImpuestoIVA(iva);
                    producto.setImpuestoIEPS(ieps);

                    productoRepository.save(producto);
                    resultados.add(producto.getDto());

                } catch (Exception ex) {
                    System.out.println("Error en fila " + i + ": " + ex.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }

        return resultados;
    }

    public void exportarProductosAExcel(List<Long> ids, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            // Encabezados
            String[] headers = {
                    "nombre", "descripcion", "precio", "categoria",
                    "unidad_medida", "impuesto_iva", "impuesto_ieps", "activo"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Obtener productos
            List<Producto> productos = productoRepository.findAllById(ids);

            int rowIdx = 1;
            for (Producto producto : productos) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(producto.getNombre());
                row.createCell(1).setCellValue(producto.getDescripcion());
                row.createCell(2).setCellValue(producto.getPrecio().doubleValue());
                row.createCell(3).setCellValue(producto.getCategoria().getNombre());
                row.createCell(4).setCellValue(producto.getUnidadMedida().getNombre());
                row.createCell(5).setCellValue(producto.getImpuestoIVA().getNombre());
                row.createCell(6).setCellValue(
                        producto.getImpuestoIEPS() != null ? producto.getImpuestoIEPS().getNombre() : "");
                row.createCell(7).setCellValue(producto.getActivo());
            }

            // Configurar respuesta HTTP
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=productos.xlsx");

            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

   public void exportarProductosAPdf(List<Long> ids, HttpServletResponse response) {
    try {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=productos.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Título
        document.add(new Paragraph("Listado de Productos - SICOV")
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));

        // Fecha
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        document.add(new Paragraph("Fecha de generación: " + fecha)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

        // Tabla con estilo
        Table table = new Table(UnitValue.createPercentArray(new float[] { 2, 3, 2, 2, 2, 2, 2, 1 }))
                .useAllAvailableWidth()
                .setFontSize(9);

        String[] headers = {
                "Nombre", "Descripción", "Precio", "Categoría",
                "Unidad", "IVA", "IEPS", "Activo"
        };

        for (String header : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));
        }

        List<Producto> productos = productoRepository.findAllById(ids);

        for (Producto p : productos) {
            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getNombre()))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getDescripcion()))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getPrecio().toPlainString()))
                .setTextAlignment(TextAlignment.RIGHT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getCategoria().getNombre()))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getUnidadMedida().getNombre()))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getImpuestoIVA().getNombre()))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getImpuestoIEPS() != null ? p.getImpuestoIEPS().getNombre() : ""))
                .setTextAlignment(TextAlignment.LEFT));

            table.addCell(new com.itextpdf.layout.element.Cell()
                .add(new Paragraph(p.getActivo() ? "Sí" : "No"))
                .setTextAlignment(TextAlignment.CENTER));
        }

        document.add(table);

        // Pie de página
        document.showTextAligned(
                new Paragraph("SICOV - Sistema de Control de Ventas e Inventario © Agroservin 2025")
                        .setFontSize(8),
                297, 20, pdf.getNumberOfPages(),
                TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0
        );

        document.close();

    } catch (IOException e) {
        throw new RuntimeException("Error al generar el PDF", e);
    }
}

}
