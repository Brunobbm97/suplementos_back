package com.suplementos.erp_suplementos.modules.product.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Location sourceLocation; // De onde saiu

    @ManyToOne
    private Location destinationLocation; // Para onde foi

    private Integer quantity;
    private LocalDateTime movementDate;
    private String type; // "TRANSFER", "ENTRY", "SALE", "ADJUSTMENT"
    private String observation;
}