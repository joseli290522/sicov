package com.jose.sicov.service.impl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.jose.sicov.dto.CategoriaDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Categoria;
import com.jose.sicov.repository.CategoriaRepository;
import com.jose.sicov.service.interfaces.ICategoriaService;
import com.jose.sicov.specification.CategoriaSpecification;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private CategoriaRepository categoriaRepository;

    @Override
    public Page<CategoriaDTO> listar(String nombre, Boolean activo, Pageable pageable) {
        Specification<Categoria> spec = CategoriaSpecification.filtrar(nombre, activo);
        return categoriaRepository.findAll(spec, pageable).map(Categoria::getDto);
    }

    @Override
    public CategoriaDTO guardar(CategoriaDTO dto) {
        if (categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setData(dto);
        return categoriaRepository.save(categoria).getDto();
    }

    @Override
    public CategoriaDTO actualizar(CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría con el ID " + dto.getId() + " no encontrada"));

        if (!categoria.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                categoriaRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        categoria.setData(dto);
        return categoriaRepository.save(categoria).getDto();

    }

    @Override
    public CategoriaDTO eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con el ID " + id + " no encontrada"));

        categoria.setEliminado(true);
        categoria.setActivo(false);
        return categoriaRepository.save(categoria).getDto();
    }

    @Override
    public CategoriaDTO cambiarEstado(Long id, boolean activo) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría con el ID " + id + " no encontrada"));

        categoria.setActivo(activo);
        return categoriaRepository.save(categoria).getDto();
    }

    public List<CategoriaDTO> importarCategoriasDesdeExcel(MultipartFile file) {
        List<CategoriaDTO> resultados = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                
                if (row == null)
                    continue;

                try {
                    String nombre = row.getCell(0).getStringCellValue().trim();
                    String descripcion = row.getCell(1).getStringCellValue().trim();

                    Boolean activo;
                    Cell activoCell = row.getCell(2);
                    if (activoCell.getCellType() == CellType.BOOLEAN) {
                        activo = activoCell.getBooleanCellValue();
                    } else if (activoCell.getCellType() == CellType.STRING) {
                        activo = Boolean.parseBoolean(activoCell.getStringCellValue().trim().toLowerCase());
                    } else {
                        throw new IllegalArgumentException("Activo inválido en fila " + i);
                    }

                    // Validar duplicado
                    if (categoriaRepository.findByNombre(nombre).isPresent()) {
                        throw new IllegalArgumentException("Ya existe la categoría: " + nombre);
                    }

                    CategoriaDTO dto = CategoriaDTO.builder()
                            .nombre(nombre)
                            .descripcion(descripcion)
                            .activo(activo)
                            .build();

                    Categoria categoria = new Categoria();
                    categoria.setData(dto);
                    categoriaRepository.save(categoria);
                    resultados.add(categoria.getDto());

                } catch (Exception ex) {
                    System.out.println(" Error en fila " + i + ": " + ex.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }

        return resultados;
    }

    public void exportarCategoriasAExcel(List<Long> ids, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Categorías");

            // Estilo de encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Nombre" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Categoria> categorias = categoriaRepository.findAllById(ids);

            // Datos
            for (int i = 0; i < categorias.size(); i++) {
                Categoria c = categorias.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getNombre());
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar respuesta
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=categorias.xlsx");

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el Excel de categorías", e);
        }
    }

    public void exportarCategoriasAPdf(List<Long> ids, HttpServletResponse response) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=categorias.pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Listado de Categorías - SICOV")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));

            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("Fecha de generación: " + fecha)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20));

            com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(
                    UnitValue.createPercentArray(new float[] { 1, 4 }))
                    .useAllAvailableWidth()
                    .setFontSize(9);

            String[] headers = { "ID", "Nombre" };

            for (String header : headers) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            List<Categoria> categorias = categoriaRepository.findAllById(ids);

            for (Categoria c : categorias) {
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.valueOf(c.getId())))
                        .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getNombre()))
                        .setTextAlignment(TextAlignment.LEFT));
            }

            document.add(table);

            document.showTextAligned(
                    new Paragraph("SICOV - Sistema de Control de Ventas e Inventario © Agroservin 2025")
                            .setFontSize(8),
                    297, 20, pdf.getNumberOfPages(),
                    TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);

            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF de categorías", e);
        }
    }

    

}
