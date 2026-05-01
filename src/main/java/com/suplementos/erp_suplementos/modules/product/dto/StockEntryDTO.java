package com.suplementos.erp_suplementos.modules.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockEntryDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long productId;

    @NotNull(message = "O ID da localização é obrigatória (ex: Loja, Depósito)")
    private Long locationId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade de entrada deve ser pelo menos 1")
    private Integer quantity;

    @NotNull(message = "O custo TOTAL pago por essa quantidade é obrigatório")
    @Positive
    private BigDecimal totalCost; // Valor que ele pagou na caixa/lote

    @NotNull(message = "A data de validade é obrigatória")
    private LocalDate expirationDate;

    // A Nota Fiscal é opcional, mas recomendada para auditoria
    private String invoiceNumber;

    // Opcional: Se ele quiser aproveitar a entrada para mudar o preço da prateleira
    private BigDecimal newSalePrice;
}