package com.suplementos.erp_suplementos.modules.sales.dto.response;

import com.suplementos.erp_suplementos.modules.sales.enums.PaymentMethod;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleResponseDTO {
    private Long id;
    private String locationName; // O Front-end quer o nome da loja, não só o ID
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt; // Data e hora da venda
    private List<SaleItemResponseDTO> items; // Lista de produtos vendidos
}