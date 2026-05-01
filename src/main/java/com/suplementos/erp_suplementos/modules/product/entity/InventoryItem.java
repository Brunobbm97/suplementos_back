package com.suplementos.erp_suplementos.modules.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer quantity = 0;
    private Integer inTransitQuantity = 0;

    private LocalDate expirationDate; // Validade deste lote específico neste local
}