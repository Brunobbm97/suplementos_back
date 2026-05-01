package com.suplementos.erp_suplementos.modules.product.mapper;

import com.suplementos.erp_suplementos.modules.product.dto.LocationDTO;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    Location toEntity(LocationDTO dto);

    LocationDTO toDTO(Location entity);

    // Atualiza uma entidade existente com os dados do DTO
    void updateEntityFromDTO(LocationDTO dto, @MappingTarget Location entity);
}