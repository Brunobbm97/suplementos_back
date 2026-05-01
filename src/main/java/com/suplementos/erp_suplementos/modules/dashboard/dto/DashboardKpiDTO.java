package com.suplementos.erp_suplementos.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class DashboardKpiDTO {
    private BigDecimal totalRevenue;      // Receita Total
    private BigDecimal totalExpense;      // Despesa Total
    private BigDecimal cashBalance;       // Saldo em Caixa (Cálculo em tempo real)
    private long expiringProductsCount;   // Alerta de Validade (Próximos 30 dias)
}