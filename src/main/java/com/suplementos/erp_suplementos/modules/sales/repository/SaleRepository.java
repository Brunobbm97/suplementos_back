package com.suplementos.erp_suplementos.modules.sales.repository;

import com.suplementos.erp_suplementos.modules.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {}