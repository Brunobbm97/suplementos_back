package com.suplementos.erp_suplementos.modules.dashboard.service;

import com.suplementos.erp_suplementos.modules.dashboard.dto.*;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.financial.repository.FinancialTransactionRepository;
import com.suplementos.erp_suplementos.modules.product.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialTransactionRepository transactionRepository;
    private final InventoryService inventoryService;

    @Transactional(readOnly = true)
    public DashboardResponseDTO getSummary(String period) {

        // 1. DEFINIÇÃO DO PERÍODO DINÂMICO
        LocalDateTime start;
        LocalDateTime now = LocalDateTime.now();

        switch (period.toUpperCase()) {
            case "TODAY":
                start = LocalDate.now().atStartOfDay();
                break;
            case "MONTH":
                start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
                break;
            case "WEEK":
            default:
                start = LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
                break;
        }

        // 2. BUSCAR KPIs FILTRADOS PELO PERÍODO
        // Agora sim chamando o método com filtro de datas!
        BigDecimal revenue = transactionRepository.sumPaidAmountByTypeAndDate(TransactionType.REVENUE, start, now);
        BigDecimal expense = transactionRepository.sumPaidAmountByTypeAndDate(TransactionType.EXPENSE, start, now);

        // Tratamento de nulos (caso não haja vendas no período, o banco retorna null)
        revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
        expense = (expense != null) ? expense : BigDecimal.ZERO;

        // Alertas de validade (mantemos os 30 dias de threshold)
        long expiringCount = (long) inventoryService.getExpiringProducts(30).size();

        DashboardKpiDTO kpis = DashboardKpiDTO.builder()
                .totalRevenue(revenue)
                .totalExpense(expense)
                .cashBalance(revenue.subtract(expense)) // O saldo do card também refletirá apenas o período
                .expiringProductsCount(expiringCount)
                .build();

        // 3. BUSCAR TENDÊNCIAS (Filtradas pelo início do período escolhido)
        List<Object[]> dailyData = transactionRepository.findDailyPaidRevenue(start);

        List<DailyRevenueDTO> dailyList = dailyData.stream()
                .map(obj -> new DailyRevenueDTO(
                        obj[0].toString(),
                        (BigDecimal) obj[1]
                ))
                .collect(Collectors.toList());

        DashboardTrendDTO trends = new DashboardTrendDTO(dailyList);

        // ADCIONANDO COMENTARIO
        // 4. RETORNAR COMPOSIÇÃO FINAL
        return DashboardResponseDTO.builder()
                .kpis(kpis)
                .trends(trends)
                .build();
    }
}