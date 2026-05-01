package com.suplementos.erp_suplementos.modules.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String sku; // Código único do produto

    @Column(columnDefinition = "TEXT") // Permite textos longos
    private String description;

    private String brand;
    private String flavor;
    private BigDecimal salePrice;
    private BigDecimal costPrice;
}
