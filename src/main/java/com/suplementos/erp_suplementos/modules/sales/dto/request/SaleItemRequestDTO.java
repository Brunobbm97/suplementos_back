package com.suplementos.erp_suplementos.modules.sales.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemRequestDTO {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long productId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade vendida deve ser no mínimo 1")
    private Integer quantity;

    @NotNull(message = "O preço unitário é obrigatório")
    private BigDecimal unitPrice;
}