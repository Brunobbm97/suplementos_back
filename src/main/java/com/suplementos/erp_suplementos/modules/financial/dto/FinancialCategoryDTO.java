package com.suplementos.erp_suplementos.modules.financial.dto;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinancialCategoryDTO {

    private Long id; // Útil para a tabela do Front-end

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;

    @NotNull(message = "O tipo da transação (Receita ou Despesa) é obrigatório")
    private TransactionType type;
}