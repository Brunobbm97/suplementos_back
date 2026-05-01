package com.suplementos.erp_suplementos.modules.product.dto.Response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InventoryResponseDTO {
    private Long inventoryId;
    private Long productId;
    private String sku;
    private String productName;
    private String brand;
    private String locationName;
    private Integer quantity;
    private Integer inTransitQuantity;
    private LocalDate expirationDate;
    private BigDecimal salePrice;
}