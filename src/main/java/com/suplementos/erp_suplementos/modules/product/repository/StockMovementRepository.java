package com.suplementos.erp_suplementos.modules.product.repository;

import com.suplementos.erp_suplementos.modules.product.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    // Aqui futuramente poderemos buscar o histórico por produto ou por local

    // Busca tudo ordenando pela data mais recente (descendente)
    List<StockMovement> findAllByOrderByMovementDateDesc();
}