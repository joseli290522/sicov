package com.jose.sicov.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.jose.sicov.dto.ProductoDTO;
import com.jose.sicov.service.impl.ProductoServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/productos")
@AllArgsConstructor
public class ProductoController {

    private ProductoServiceImpl productoService;

    @GetMapping
    public ResponseEntity<Page<ProductoDTO>> listar(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) Boolean activo,
        @RequestParam(required = false) String nombre,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(productoService.listar(categoria, activo, nombre, pageable));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@RequestBody ProductoDTO dto) {
        ProductoDTO nuevo = productoService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping
    public ResponseEntity<ProductoDTO> actualizar(@RequestBody ProductoDTO dto) {
        ProductoDTO actualizado = productoService.actualizar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProductoDTO> eliminar(@PathVariable Long id) {
        ProductoDTO eliminado = productoService.eliminar(id);
        return ResponseEntity.ok(eliminado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductoDTO> cambiarEstado(@PathVariable Long id, @RequestBody boolean activo) {
        ProductoDTO actualizado = productoService.cambiarEstado(id, activo);
        return ResponseEntity.ok(actualizado);
    }
    
    @PostMapping("/importar")
    public ResponseEntity<List<ProductoDTO>> importar(@RequestParam("file") MultipartFile file) {
        List<ProductoDTO> productos = productoService.importarProductosDesdeExcel(file);
        return ResponseEntity.ok(productos);
    }

    @PostMapping("/exportar-excel")
    public void exportarProductos(@RequestBody List<Long> ids, HttpServletResponse response) {
        productoService.exportarProductosAExcel(ids, response);
    }

    @PostMapping("/exportar-pdf")
    public void exportarProductosPdf(@RequestBody List<Long> ids, HttpServletResponse response) {
        productoService.exportarProductosAPdf(ids, response);
    }

}
