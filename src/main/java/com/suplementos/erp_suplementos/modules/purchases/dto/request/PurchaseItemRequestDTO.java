package com.suplementos.erp_suplementos.modules.purchases.dto.request;

import lombok.Data;

@Data
public class PurchaseItemRequestDTO {
    private Long productId;
    private Integer quantity;
    private java.math.BigDecimal unitCost;
}