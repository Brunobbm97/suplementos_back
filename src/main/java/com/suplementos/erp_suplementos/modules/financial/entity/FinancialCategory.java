package com.suplementos.erp_suplementos.modules.financial.entity;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "financial_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FinancialCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // Define se esta categoria é para Receitas ou Despesas
}