package com.suplementos.erp_suplementos.modules.dashboard.controller;

import com.suplementos.erp_suplementos.modules.dashboard.dto.DashboardResponseDTO;
import com.suplementos.erp_suplementos.modules.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getSummary(
            @RequestParam(name = "period", defaultValue = "WEEK") String period) {
        return ResponseEntity.ok(dashboardService.getSummary(period));
    }
}