package com.suplementos.erp_suplementos.modules.product.controller;

import com.suplementos.erp_suplementos.modules.product.dto.Response.InventoryResponseDTO;
import com.suplementos.erp_suplementos.modules.product.dto.Response.StockMovementResponseDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockEntryDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockTransferDTO;
import com.suplementos.erp_suplementos.modules.product.entity.InventoryItem;
import com.suplementos.erp_suplementos.modules.product.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Listagem geral de estoque
    @GetMapping("/stock")
    public ResponseEntity<List<InventoryResponseDTO>> getAllStock() {
        return ResponseEntity.ok(inventoryService.findAllStock());
    }

    // Entrada de NF (Mapeado corretamente para InventoryService)
    @PostMapping("/stock/entries")
    public ResponseEntity<?> registerStockEntry(@Valid @RequestBody StockEntryDTO entryDTO) {
        try {
            inventoryService.registerStockEntry(entryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Transferência Direta (A que estamos usando agora)
    @PostMapping("/transfer")
    public ResponseEntity<?> transferStock(@RequestBody StockTransferDTO dto) {
        try {
            inventoryService.executeDirectTransfer(dto);
            return ResponseEntity.ok(Map.of("message", "Transferência realizada com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Histórico de Movimentações (Auditoria)
    @GetMapping("/movements")
    public ResponseEntity<List<StockMovementResponseDTO>> getMovements() {
        return ResponseEntity.ok(inventoryService.findAllMovements());
    }

    // Pesquisa para o PDV (Frente de Caixa)
    @GetMapping("/stock/pos-search")
    public ResponseEntity<List<InventoryResponseDTO>> searchForPos(
            @RequestParam Long locationId,
            @RequestParam String query) {
        return ResponseEntity.ok(inventoryService.searchStockForPos(locationId, query));
    }

    // Relatório de Validade
    @GetMapping("/reports/expiring")
    public ResponseEntity<List<InventoryItem>> getExpiringItems(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(inventoryService.getExpiringProducts(days));
    }
}