package com.suplementos.erp_suplementos.modules.financial.controller;

import com.suplementos.erp_suplementos.modules.financial.dto.FinancialSummaryDTO;
import com.suplementos.erp_suplementos.modules.financial.dto.FinancialTransactionResponseDTO;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/financial")
@RequiredArgsConstructor
public class FinancialController {

    private final FinancialService financialService;

    @GetMapping("/balance")
    public FinancialSummaryDTO getBalance() {
        return financialService.getBalanceSummary();
    }

    // Busca a lista para a Agenda
    @GetMapping
    public ResponseEntity<List<FinancialTransactionResponseDTO>> getAll() {
        return ResponseEntity.ok(financialService.findAll());
    }

    // Endpoint para o botão "Confirmar Pagamento" (Baixa)
    // Usamos PATCH pois estamos alterando apenas o status e a data de pagamento
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmPayment(@PathVariable Long id) {
        financialService.confirmPayment(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para o formulário de "Novo Lançamento" da Agenda
    @PostMapping("/advanced")
    public ResponseEntity<Void> registerAdvanced(@Valid @RequestBody FinancialTransactionResponseDTO dto) {
        financialService.registerAdvancedTransaction(
                dto.getDescription(),
                dto.getAmount(),
                dto.getType(),
                dto.getCategoryName(),
                null,
                dto.getDueDate(),
                dto.getStatus(),
                dto.getNotes()
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody FinancialTransactionResponseDTO dto) {
        financialService.updateTransaction(id, dto);
        return ResponseEntity.ok().build();
    }
}