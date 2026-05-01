package com.suplementos.erp_suplementos.modules.sales.entity;

import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.sales.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sales")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location; // A loja onde a venda ocorreu

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> items;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
}