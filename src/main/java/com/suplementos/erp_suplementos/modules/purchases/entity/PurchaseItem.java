package com.suplementos.erp_suplementos.modules.purchases.entity;

import com.suplementos.erp_suplementos.modules.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;
    private BigDecimal unitCost; // Preço pago por unidade
}