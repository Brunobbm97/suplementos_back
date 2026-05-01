package com.suplementos.erp_suplementos.modules.financial.dto;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionStatus;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import jakarta.validation.constraints.*; // Importação das validações
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialTransactionResponseDTO {

    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 3, max = 100, message = "A descrição deve ter entre 3 e 100 caracteres")
    private String description;

    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal amount;

    @NotNull(message = "O tipo (Receita/Despesa) é obrigatório")
    private TransactionType type;

    @NotNull(message = "O status é obrigatório")
    private TransactionStatus status;

    @NotBlank(message = "A categoria é obrigatória")
    private String categoryName;

    @NotNull(message = "A data de vencimento é obrigatória")
    @FutureOrPresent(message = "A data de vencimento não pode ser no passado")
    private LocalDateTime dueDate;

    private LocalDateTime paymentDate;
    private String notes;
}