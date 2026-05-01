package com.suplementos.erp_suplementos.modules.sales.mapper;

import com.suplementos.erp_suplementos.modules.sales.dto.request.SaleRequestDTO;
import com.suplementos.erp_suplementos.modules.sales.dto.response.SaleItemResponseDTO;
import com.suplementos.erp_suplementos.modules.sales.dto.response.SaleResponseDTO;
import com.suplementos.erp_suplementos.modules.sales.entity.Sale;
import com.suplementos.erp_suplementos.modules.sales.entity.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    // Transforma o Request em Entidade (Cabeçalho da Venda)
    @Mapping(target = "location.id", source = "locationId")
    @Mapping(target = "items", ignore = true) // Ignoramos para a Service tratar o estoque manualmente
    @Mapping(target = "totalAmount", ignore = true) // Será calculado na Service
    Sale toEntity(SaleRequestDTO dto);

    // --- SAÍDA (RESPONSE) ---

    // Mapeia a Venda Principal
    @Mapping(target = "locationName", source = "location.name")
    SaleResponseDTO toResponseDTO(Sale entity);

    // Mapeia os Itens de dentro da Venda
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    SaleItemResponseDTO toItemResponseDTO(SaleItem entity);
}