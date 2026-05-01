package com.suplementos.erp_suplementos.modules.financial.repository;

import com.suplementos.erp_suplementos.modules.financial.entity.FinancialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FinancialCategoryRepository extends JpaRepository<FinancialCategory, Long> {
    Optional<FinancialCategory> findByName(String name);

    // A mágica do Spring Data JPA: ele lê "existsBy" + "Name" e monta o SQL de busca sozinho!
    boolean existsByName(String name);
}