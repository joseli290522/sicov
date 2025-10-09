package com.jose.sicov.service.impl;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.jose.sicov.dto.ClienteDTO;
import com.jose.sicov.exception.RecursoNoEncontradoException;
import com.jose.sicov.model.Cliente;
import com.jose.sicov.repository.ClienteRepository;
import com.jose.sicov.service.interfaces.IClienteService;
import com.jose.sicov.specification.ClienteSpecification;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ClienteServiceImpl implements IClienteService {

    private ClienteRepository clienteRepository;

    @Override
    public Page<ClienteDTO> listar(String nombre, String contacto, Boolean activo, Pageable pageable) {
        Specification<Cliente> spec = ClienteSpecification.filtrar(nombre, contacto, activo);
        return clienteRepository.findAll(spec, pageable).map(Cliente::getDto);
    }

    @Override
    public ClienteDTO guardar(ClienteDTO dto) {

        if (clienteRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
        }

        if (clienteRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo electrónico");
        }

        Cliente cliente = new Cliente();
        cliente.setData(dto);
        return clienteRepository.save(cliente).getDto();
    }

    @Override
    public ClienteDTO actualizar(ClienteDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getId())
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Cliente con el ID " + dto.getId() + " no encontrado"));

        if (!cliente.getNombre().equalsIgnoreCase(dto.getNombre())
                && clienteRepository.findByNombre(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
        }

        if (!cliente.getCorreo().equalsIgnoreCase(dto.getCorreo())
                && clienteRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo electrónico");
        }

        cliente.setData(dto);
        return clienteRepository.save(cliente).getDto();
    }

    @Override
    public ClienteDTO eliminar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con el ID " + id + " no encontrado"));

        cliente.setEliminado(true);
        cliente.setActivo(false);
        return clienteRepository.save(cliente).getDto();
    }

    @Override
    public ClienteDTO cambiarEstado(Long id, boolean activo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con el ID " + id + " no encontrado"));

        cliente.setActivo(activo);
        return clienteRepository.save(cliente).getDto();
    }

    public List<ClienteDTO> importarClientesDesdeExcel(MultipartFile file) {
        List<ClienteDTO> resultados = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                try {
                    String nombre = row.getCell(0).getStringCellValue().trim();
                    String contacto = row.getCell(1).getStringCellValue().trim();
                    String correo = row.getCell(2).getStringCellValue().trim();
                    String direccion = row.getCell(3).getStringCellValue().trim();

                    Boolean activo;
                    Cell activoCell = row.getCell(4);
                    if (activoCell.getCellType() == CellType.BOOLEAN) {
                        activo = activoCell.getBooleanCellValue();
                    } else if (activoCell.getCellType() == CellType.STRING) {
                        activo = Boolean.parseBoolean(activoCell.getStringCellValue().trim().toLowerCase());
                    } else {
                        throw new IllegalArgumentException("Activo inválido en fila " + i);
                    }

                    // Validar duplicado por nombre o correo
                    if (clienteRepository.findByNombre(nombre).isPresent()) {
                        throw new IllegalArgumentException("Ya existe un cliente con ese nombre");
                    }

                    if (clienteRepository.findByCorreo(correo).isPresent()) {
                        throw new IllegalArgumentException("Ya existe un cliente con ese correo electrónico");
                    }

                    ClienteDTO dto = ClienteDTO.builder()
                            .nombre(nombre)
                            .contacto(contacto)
                            .correo(correo)
                            .direccion(direccion)
                            .activo(activo)
                            .build();

                    Cliente cliente = new Cliente();
                    cliente.setData(dto);
                    clienteRepository.save(cliente);
                    resultados.add(cliente.getDto());

                } catch (Exception ex) {
                    System.out.println("Error en fila " + i + ": " + ex.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel", e);
        }

        return resultados;
    }

    public void exportarClientesAExcel(List<Long> ids, HttpServletResponse response) {

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");

            // Estilo de encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Nombre", "Contacto", "Correo", "Dirección", "Activo" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Cliente> clientes = clienteRepository.findAllById(ids);

            // Datos
            for (int i = 0; i < clientes.size(); i++) {
                Cliente c = clientes.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(c.getId());
                row.createCell(1).setCellValue(c.getNombre());
                row.createCell(2).setCellValue(c.getContacto() != null ? c.getContacto() : "");
                row.createCell(3).setCellValue(c.getCorreo() != null ? c.getCorreo() : "");
                row.createCell(4).setCellValue(c.getDireccion() != null ? c.getDireccion() : "");
                row.createCell(5).setCellValue(c.getActivo() ? "Sí" : "No");
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Configurar respuesta
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=clientes.xlsx");

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el Excel de clientes", e);
        }
    }

    public void exportarClientesAPdf(List<Long> ids, HttpServletResponse response) {

        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=clientes.pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            document.add(new Paragraph("Listado de Clientes - SICOV")
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
                    UnitValue.createPercentArray(new float[] { 1, 3, 3, 4, 4, 1 }))
                    .useAllAvailableWidth()
                    .setFontSize(9);

            String[] headers = { "ID", "Nombre", "Contacto", "Correo", "Dirección", "Activo" };

            for (String header : headers) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(header).setBold().setFontColor(ColorConstants.WHITE))
                        .setBackgroundColor(ColorConstants.GRAY)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            //List<Cliente> clientes = clienteRepository.findAllById(ids);
            List<Cliente> clientes = clienteRepository.findAll();

            for (Cliente c : clientes) {
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.valueOf(c.getId())))
                        .setTextAlignment(TextAlignment.CENTER));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getNombre()))
                        .setTextAlignment(TextAlignment.LEFT));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getContacto() != null ? c.getContacto() : ""))
                        .setTextAlignment(TextAlignment.LEFT));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getCorreo() != null ? c.getCorreo() : ""))
                        .setTextAlignment(TextAlignment.LEFT));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getDireccion() != null ? c.getDireccion() : ""))
                        .setTextAlignment(TextAlignment.LEFT));

                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(c.getActivo() ? "Sí" : "No"))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            document.add(table);

            document.showTextAligned(
                    new Paragraph("SICOV - Sistema de Control de Ventas e Inventario © Agroservin 2025")
                            .setFontSize(8),
                    297, 20, pdf.getNumberOfPages(),
                    TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0);

            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el PDF de clientes", e);
        }
    }

}
