package com.suplementos.erp_suplementos.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyRevenueDTO {
    private String date;
    private BigDecimal amount;
}