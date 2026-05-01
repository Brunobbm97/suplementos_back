package com.suplementos.erp_suplementos.modules.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String name;

    // O SKU pode ser nulo na criação (pois o Back gera),
    // mas o DTO o transporta na listagem e edição.
    private String sku;

    @NotBlank(message = "A marca é obrigatória")
    private String brand;

    private String flavor; // Sabor (Whey de Chocolate, Creatina sem sabor, etc)

    @NotNull(message = "O preço de venda é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal salePrice;

    private String description;
}