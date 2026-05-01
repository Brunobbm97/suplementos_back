package com.suplementos.erp_suplementos.modules.purchases.controller;

import com.suplementos.erp_suplementos.modules.purchases.dto.request.PurchaseRequestDTO;
import com.suplementos.erp_suplementos.modules.purchases.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
@Tag(name = "Purchases", description = "Gerenciamento de compras e entrada de estoque")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @Operation(summary = "Registrar uma nova compra", description = "Aumenta o estoque e gera uma despesa no financeiro")
    public ResponseEntity<String> registerPurchase(@RequestBody PurchaseRequestDTO request) {
        try {
            purchaseService.registerPurchase(request);
            return ResponseEntity.ok("Compra registrada e estoque abastecido com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}