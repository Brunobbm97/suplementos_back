package com.suplementos.erp_suplementos.modules.financial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class FinancialSummaryDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
}