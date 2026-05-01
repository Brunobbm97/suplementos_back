package com.suplementos.erp_suplementos.modules.financial.mapper;

import com.suplementos.erp_suplementos.modules.financial.dto.FinancialCategoryDTO;
import com.suplementos.erp_suplementos.modules.financial.entity.FinancialCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FinancialCategoryMapper {

    FinancialCategory toEntity(FinancialCategoryDTO dto);

    FinancialCategoryDTO toDTO(FinancialCategory entity);

    // 👇 A MÁGICA ESTÁ AQUI: Ignorar o ID na hora de fazer o update!
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(FinancialCategoryDTO dto, @MappingTarget FinancialCategory entity);
}