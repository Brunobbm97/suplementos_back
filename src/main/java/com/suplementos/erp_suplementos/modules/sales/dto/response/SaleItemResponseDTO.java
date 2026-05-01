package com.suplementos.erp_suplementos.modules.sales.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SaleItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName; // O Front-end precisa do nome para mostrar na tela
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}