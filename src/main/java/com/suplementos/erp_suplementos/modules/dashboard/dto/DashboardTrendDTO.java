package com.suplementos.erp_suplementos.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardTrendDTO {
    private List<DailyRevenueDTO> dailyRevenue;
}