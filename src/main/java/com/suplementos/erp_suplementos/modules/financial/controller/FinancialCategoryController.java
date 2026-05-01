package com.suplementos.erp_suplementos.modules.financial.controller;

import com.suplementos.erp_suplementos.modules.financial.dto.FinancialCategoryDTO;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/financial-categories")
@RequiredArgsConstructor
public class FinancialCategoryController {

    private final FinancialCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<FinancialCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FinancialCategoryDTO dto) {
        try {
            FinancialCategoryDTO saved = categoryService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody FinancialCategoryDTO dto) {
        try {
            return ResponseEntity.ok(categoryService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}