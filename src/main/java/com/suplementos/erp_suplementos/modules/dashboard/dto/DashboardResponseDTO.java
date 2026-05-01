package com.suplementos.erp_suplementos.modules.dashboard.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponseDTO {
    private DashboardKpiDTO kpis;
    private DashboardTrendDTO trends;
}