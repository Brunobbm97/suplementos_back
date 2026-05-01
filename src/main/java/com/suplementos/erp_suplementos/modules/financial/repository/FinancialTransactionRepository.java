package com.suplementos.erp_suplementos.modules.financial.repository;

import com.suplementos.erp_suplementos.modules.financial.entity.FinancialTransaction;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionStatus;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {

    // 1. Soma Real (Dinheiro em Caixa): Filtra apenas o que já foi PAGO
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t WHERE t.type = :type AND t.status = 'PAGO'")
    BigDecimal sumPaidAmountByType(@Param("type") TransactionType type);

    // 2. Soma Real por Período: Usa a data do pagamento real (paymentDate)
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t " +
            "WHERE t.type = :type AND t.status = 'PAGO' " +
            "AND t.paymentDate BETWEEN :start AND :end")
    BigDecimal sumPaidAmountByTypeAndDate(@Param("type") TransactionType type,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    // 3. Gráfico de Faturamento: Considera apenas vendas PAGAS pela data do pagamento
    @Query("SELECT CAST(t.paymentDate AS date) as date, SUM(t.amount) as total " +
            "FROM FinancialTransaction t " +
            "WHERE t.type = 'REVENUE' AND t.status = 'PAGO' AND t.paymentDate >= :startDate " +
            "GROUP BY CAST(t.paymentDate AS date) " +
            "ORDER BY CAST(t.paymentDate AS date) ASC")
    List<Object[]> findDailyPaidRevenue(@Param("startDate") LocalDateTime startDate);

    // 4. NOVO: Soma de Pendências (Previsão de Futuro): Filtra o que o dono ainda tem que pagar/receber
    @Query("SELECT SUM(t.amount) FROM FinancialTransaction t " +
            "WHERE t.type = :type AND t.status = 'PENDENTE' " +
            "AND t.dueDate BETWEEN :start AND :end")
    BigDecimal sumPendingAmountByDueDate(@Param("type") TransactionType type,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    // Busca todas as transações e as coloca na ordem: as que vencem primeiro aparecem antes
    List<FinancialTransaction> findAllByOrderByDueDateAsc();
}