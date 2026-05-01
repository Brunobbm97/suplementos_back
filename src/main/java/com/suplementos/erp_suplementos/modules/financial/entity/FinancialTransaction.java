package com.suplementos.erp_suplementos.modules.financial.entity;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionStatus;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder // Essencial para a sobrecarga de métodos
public class FinancialTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private BigDecimal amount;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime dueDate;      // Pode ser nulo no banco
    private LocalDateTime paymentDate;  // Pode ser nulo no banco

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private FinancialCategory category;

    private Long referenceId;

    @Column(columnDefinition = "TEXT")
    private String notes;
}