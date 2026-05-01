package com.suplementos.erp_suplementos.modules.product.service;

import com.suplementos.erp_suplementos.modules.product.dto.ProductDTO;
import com.suplementos.erp_suplementos.modules.product.dto.Response.InventoryResponseDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockEntryDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockTransferDTO;
import com.suplementos.erp_suplementos.modules.product.entity.InventoryItem;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.product.entity.Product;
import com.suplementos.erp_suplementos.modules.product.mapper.ProductMapper;
import com.suplementos.erp_suplementos.modules.product.repository.InventoryItemRepository;
import com.suplementos.erp_suplementos.modules.product.repository.LocationRepository;
import com.suplementos.erp_suplementos.modules.product.repository.ProductRepository;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialService;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final InventoryItemRepository inventoryItemRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository; // Adicionado
    private final ProductMapper productMapper;           // Adicionado

    /**
     * Busca todos os produtos e converte para DTO.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com o ID: " + id));

        // Atualizamos apenas os campos editáveis
        existingProduct.setName(dto.getName());
        existingProduct.setBrand(dto.getBrand());
        existingProduct.setFlavor(dto.getFlavor());
        existingProduct.setSalePrice(dto.getSalePrice());
        existingProduct.setDescription(dto.getDescription());
        // Observe que NÃO fazemos existingProduct.setSku(...) aqui.

        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    /**
     * Remove um produto do sistema.
     * Importante: Em um sistema real, você pode preferir um "Soft Delete" (campo ativo=false)
     * para não perder o histórico de vendas.
     */
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produto não encontrado com o ID: " + id);
        }
        productRepository.deleteById(id);
    }




    @Transactional
    public ProductDTO save(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);

        // Gerar SKU automaticamente se for um novo produto
        if (product.getId() == null) {
            product.setSku(generateSku(product));
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    private String generateSku(Product product) {
        // Padrão: MARCA-NOME-SABOR-RANDOM
        String brand = safeSubstring(product.getBrand(), 3);
        String name = safeSubstring(product.getName(), 4);
        String flavor = (product.getFlavor() != null && !product.getFlavor().isEmpty())
                ? safeSubstring(product.getFlavor(), 3)
                : "NAO";

        String randomHash = java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        return String.format("%s-%s-%s-%s", brand, name, flavor, randomHash).toUpperCase();
    }

    private String safeSubstring(String value, int length) {
        if (value == null) return "XXX";
        String cleaned = value.replaceAll("[^a-zA-Z0-9]", "");
        return cleaned.length() >= length ? cleaned.substring(0, length) : cleaned;
    }
}