package com.suplementos.erp_suplementos.modules.sales.controller;

import com.suplementos.erp_suplementos.modules.sales.dto.request.SaleRequestDTO;
import com.suplementos.erp_suplementos.modules.sales.dto.response.SaleResponseDTO; // <-- NOVO IMPORT
import com.suplementos.erp_suplementos.modules.sales.service.SalesService;
import com.suplementos.erp_suplementos.modules.sales.enums.PaymentMethod;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SalesService salesService;

    // 👇 NOVO: Endpoint para buscar todo o histórico de vendas para a tabela do Angular
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> getAllSales() {
        return ResponseEntity.ok(salesService.findAll());
    }

    // O SEU CÓDIGO INTACTO: Criação de venda segura com tratamento de erros
    @PostMapping
    public ResponseEntity<Map<String, String>> createSale(@Valid @RequestBody SaleRequestDTO request) {
        try {
            salesService.executeSale(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Venda realizada com sucesso!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // O SEU CÓDIGO INTACTO: Alimentador dinâmico do Dropdown do PDV
    @GetMapping("/payment-methods")
    public ResponseEntity<List<Map<String, String>>> getPaymentMethods() {
        List<Map<String, String>> options = Arrays.stream(PaymentMethod.values())
                .map(pm -> {
                    String label = switch (pm) {
                        case PIX -> "PIX";
                        case CREDIT_CARD -> "Cartão de Crédito";
                        case DEBIT_CARD -> "Cartão de Débito";
                        case MONEY -> "Dinheiro Físico";
                        default -> pm.name();
                    };
                    return Map.of("label", label, "value", pm.name());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(options);
    }
}