package com.suplementos.erp_suplementos.modules.product.dto.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockMovementResponseDTO {
    private Long id;
    private String productName;
    private String sourceLocationName;
    private String destinationLocationName;
    private Integer quantity;
    private LocalDateTime movementDate;
    private String type; // TRANSFER, ENTRY, SALE
    private String observation;
}