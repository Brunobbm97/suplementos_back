package com.suplementos.erp_suplementos.config;

import com.suplementos.erp_suplementos.modules.financial.entity.FinancialCategory;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.financial.repository.FinancialCategoryRepository;
import com.suplementos.erp_suplementos.modules.product.entity.*;
import com.suplementos.erp_suplementos.modules.product.enums.LocationType;
import com.suplementos.erp_suplementos.modules.product.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;
    private final InventoryItemRepository inventoryRepository;
    private final FinancialCategoryRepository categoryRepository;


    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            // 1. Criar Depósito com endereço completo
            Location estoque = new Location();
            estoque.setName("Depósito Central");
            estoque.setType(LocationType.ESTOQUE);
            estoque.setStreet("Av. Industrial");
            estoque.setNumber("1000");
            estoque.setCity("João Pessoa");
            estoque.setState("PB");
            estoque.setPhone("8330001234");
            locationRepository.save(estoque);

            // 2. Criar Loja com endereço completo
            Location loja = new Location();
            loja.setName("Loja Centro");
            loja.setType(LocationType.LOJA);
            loja.setStreet("Rua das Palmeiras");
            loja.setNumber("42");
            loja.setNeighborhood("Centro");
            loja.setCity("João Pessoa");
            loja.setState("PB");
            loja.setPhone("83988884444");
            locationRepository.save(loja);

            categoryRepository.save(new FinancialCategory(null, "Venda de Produtos", TransactionType.REVENUE));
            categoryRepository.save(new FinancialCategory(null, "Compra de Mercadoria", TransactionType.EXPENSE));
            categoryRepository.save(new FinancialCategory(null, "Ajuste de Estoque", TransactionType.EXPENSE));

            // 3. Criar Produto usando Builder
            Product whey = Product.builder()
                    .name("Whey Protein 900g")
                    .sku("WHEY-900-CHO")
                    .brand("Optimum")
                    .flavor("Chocolate")
                    .description("Whey protein isolado de alta qualidade")
                    .salePrice(new BigDecimal("250.00"))
                    .costPrice(new BigDecimal("150.00"))
                    .build();

            whey = productRepository.save(whey);

            // 4. Criar Estoque inicial usando Builder
            inventoryRepository.save(InventoryItem.builder()
                    .product(whey)
                    .location(estoque)
                    .quantity(50)
                    .inTransitQuantity(0)
                    .expirationDate(java.time.LocalDate.now().plusYears(2)) // Adicionando validade
                    .build());

            inventoryRepository.save(InventoryItem.builder()
                    .product(whey)
                    .location(loja)
                    .quantity(5)
                    .inTransitQuantity(0)
                    .expirationDate(java.time.LocalDate.now().plusYears(1))
                    .build());

            System.out.println(">>> Banco populado com Locais detalhados!");
        }
    }
}