package com.suplementos.erp_suplementos.modules.purchases.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseRequestDTO {
    private String supplier;
    private Long destinationLocationId; // Onde os produtos vão entrar
    private List<PurchaseItemRequestDTO> items;
}