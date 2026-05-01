package com.suplementos.erp_suplementos.modules.purchases.repository;

import com.suplementos.erp_suplementos.modules.purchases.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}