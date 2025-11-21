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
import com.jose.sicov.dto.CategoriaDTO;
import com.jose.sicov.service.impl.CategoriaServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias")
@AllArgsConstructor
public class CategoriaController {

    private CategoriaServiceImpl categoriaService;

    @GetMapping
    public ResponseEntity<Page<CategoriaDTO>> listar(
        @RequestParam(required = false) String nombre,
        @RequestParam(required = false) Boolean activo,
        @PageableDefault(size = 20, sort = "nombre") Pageable pageable
    ) {
        return ResponseEntity.ok(categoriaService.listar(nombre, activo, pageable));
    }

    @PostMapping
    public ResponseEntity<CategoriaDTO> crear(@RequestBody CategoriaDTO dto) {
        CategoriaDTO nueva = categoriaService.guardar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping
    public ResponseEntity<CategoriaDTO> actualizar(@RequestBody CategoriaDTO dto) {
        CategoriaDTO actualizada = categoriaService.actualizar(dto);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaDTO> eliminar(@PathVariable Long id) {
        CategoriaDTO eliminada = categoriaService.eliminar(id);
        return ResponseEntity.ok(eliminada);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoriaDTO> cambiarEstado(@PathVariable Long id, @RequestBody boolean activo) {
        CategoriaDTO actualizada = categoriaService.cambiarEstado(id, activo);
        return ResponseEntity.ok(actualizada);
    }
    
    @PostMapping("/importar-excel")
    public ResponseEntity<List<CategoriaDTO>> importarCategorias(@RequestParam("file") MultipartFile file) {
        List<CategoriaDTO> categorias = categoriaService.importarCategoriasDesdeExcel(file);
        return ResponseEntity.ok(categorias);
    }
    
    @PostMapping("/exportar-excel")
    public void exportarCategorias(@RequestBody List<Long> ids, HttpServletResponse response) {
        categoriaService.exportarCategoriasAExcel(ids, response);
    }

    @PostMapping("/exportar-pdf")
    public void exportarCategoriasPorIds(@RequestBody List<Long> ids, HttpServletResponse response) {
        categoriaService.exportarCategoriasAPdf(ids, response);
    }

}
