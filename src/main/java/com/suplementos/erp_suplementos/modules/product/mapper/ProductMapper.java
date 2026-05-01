package com.suplementos.erp_suplementos.modules.product.mapper;

import com.suplementos.erp_suplementos.modules.product.dto.ProductDTO;
import com.suplementos.erp_suplementos.modules.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setBrand(product.getBrand());
        dto.setSalePrice(product.getSalePrice());
        return dto;
    }

    // Poderia adicionar o método toEntity se necessário
    public Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setBrand(dto.getBrand());
        product.setSalePrice(dto.getSalePrice());
        // Note: o costPrice geralmente vem de um DTO de entrada de nota/compra
        return product;
    }
}
