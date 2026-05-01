package com.suplementos.erp_suplementos.modules.product.repository;

import com.suplementos.erp_suplementos.modules.product.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    // Busca o estoque de um produto específico em um local específico
    List<InventoryItem> findByProductIdAndLocationIdOrderByExpirationDateAsc(Long productId, Long locationId);

    // Procura itens com validade antes da data X e que ainda tenham stock
    List<InventoryItem> findByExpirationDateBeforeAndQuantityGreaterThan(LocalDate date, Integer quantity);

    @Query("SELECT i FROM InventoryItem i WHERE i.location.id = :locationId " +
            "AND i.quantity > 0 " +
            "AND (LOWER(i.product.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(i.product.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<InventoryItem> findAvailableStockForPos(
            @Param("locationId") Long locationId,
            @Param("searchTerm") String searchTerm
    );

    List<InventoryItem> findByQuantityGreaterThan(Integer quantity);

    // Busca o estoque de um produto específico, em um local específico, com uma VALIDADE ESPECÍFICA
    Optional<InventoryItem> findByProductIdAndLocationIdAndExpirationDate(Long productId, Long locationId, LocalDate expirationDate);
}

