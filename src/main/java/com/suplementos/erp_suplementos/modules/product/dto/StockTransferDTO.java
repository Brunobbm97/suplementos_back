package com.suplementos.erp_suplementos.modules.product.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StockTransferDTO {
    private Long productId;
    private Long sourceLocationId;
    private Long destinationLocationId;
    private Integer quantity;
    private LocalDate expirationDate; // Fundamental para não misturar os lotes!
}