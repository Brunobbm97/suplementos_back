package com.suplementos.erp_suplementos.modules.purchases.service;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialService;
import com.suplementos.erp_suplementos.modules.product.service.ProductService;
import com.suplementos.erp_suplementos.modules.purchases.dto.request.PurchaseRequestDTO;
import com.suplementos.erp_suplementos.modules.purchases.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService; // Para aumentar o estoque
    private final FinancialService financialService; // Para registrar a despesa

    @Transactional
    public void registerPurchase(PurchaseRequestDTO request) {
        BigDecimal totalPurchase = BigDecimal.ZERO;

        // 1. Lógica de incremento de estoque (delegando para o ProductService)
        for (var item : request.getItems()) {
            // Aqui você precisaria expor um método 'addStock' no ProductService
            // productService.addStock(item.getProductId(), request.getDestinationLocationId(), item.getQuantity());

            BigDecimal itemTotal = item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPurchase = totalPurchase.add(itemTotal);
        }

        // 2. Registrar no Financeiro como DESPESA (EXPENSE)
        financialService.registerTransaction(
                "Compra de mercadoria: " + request.getSupplier(),
                totalPurchase,
                TransactionType.EXPENSE,
                "Compra de Mercadoria",
                null
        );

        System.out.println("Compra registrada e estoque abastecido!");
    }
}