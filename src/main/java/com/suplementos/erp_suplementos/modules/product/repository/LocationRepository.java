package com.suplementos.erp_suplementos.modules.product.repository;

import com.suplementos.erp_suplementos.modules.product.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    // Aqui você pode adicionar buscas customizadas futuramente,
    // como buscar por nome ou tipo.
}