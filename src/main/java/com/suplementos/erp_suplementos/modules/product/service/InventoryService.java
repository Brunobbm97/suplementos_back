package com.suplementos.erp_suplementos.modules.product.service;

import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialService;
import com.suplementos.erp_suplementos.modules.product.dto.Response.InventoryResponseDTO;
import com.suplementos.erp_suplementos.modules.product.dto.Response.StockMovementResponseDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockEntryDTO;
import com.suplementos.erp_suplementos.modules.product.dto.StockTransferDTO;
import com.suplementos.erp_suplementos.modules.product.entity.InventoryItem;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.product.entity.Product;
import com.suplementos.erp_suplementos.modules.product.entity.StockMovement;
import com.suplementos.erp_suplementos.modules.product.repository.InventoryItemRepository;
import com.suplementos.erp_suplementos.modules.product.repository.LocationRepository;
import com.suplementos.erp_suplementos.modules.product.repository.ProductRepository;
import com.suplementos.erp_suplementos.modules.product.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final LocationRepository locationRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository; // Você precisará criar este Repo
    private final FinancialService financialService;

    @org.springframework.transaction.annotation.Transactional
    public void addStock(Long productId, Long locationId, Integer amount) {
        // 1. Buscamos a lista de lotes (do que vence antes para o que vence depois)
        List<InventoryItem> items = inventoryItemRepository
                .findByProductIdAndLocationIdOrderByExpirationDateAsc(productId, locationId);

        InventoryItem inventory;

        if (!items.isEmpty()) {
            // 2. Se já existem lotes, adicionamos no primeiro da lista (o mais próximo do vencimento)
            inventory = items.get(0);
        } else {
            // 3. Se não existe nenhum lote, criamos um registro novo (sem validade definida)
            inventory = InventoryItem.builder()
                    .product(productRepository.findById(productId).orElseThrow())
                    .location(locationRepository.findById(locationId).orElseThrow())
                    .quantity(0)
                    .inTransitQuantity(0)
                    .build();
        }

        inventory.setQuantity(inventory.getQuantity() + amount);
        inventoryItemRepository.save(inventory);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<InventoryItem> getExpiringProducts(int daysThreshold) {
        LocalDate limitDate = LocalDate.now().plusDays(daysThreshold);
        return inventoryItemRepository.findByExpirationDateBeforeAndQuantityGreaterThan(limitDate, 0);
    }

    @org.springframework.transaction.annotation.Transactional
    public void registerStockEntry(StockEntryDTO dto) {
        // 1. Validar Produto e Local
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + dto.getProductId()));

        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Localização não encontrada com ID: " + dto.getLocationId()));

        // 2. Atualizar ou Criar Inventário baseando-se na VALIDADE (Gestão de Lotes)
        InventoryItem inventory = inventoryItemRepository
                .findByProductIdAndLocationIdAndExpirationDate(dto.getProductId(), dto.getLocationId(), dto.getExpirationDate())
                .orElseGet(() -> {
                    // Se não existir este produto, nesta loja, com ESTA data exata, criamos um novo LOTE
                    InventoryItem newItem = new InventoryItem();
                    newItem.setProduct(product);
                    newItem.setLocation(location);
                    newItem.setQuantity(0);
                    newItem.setInTransitQuantity(0);
                    newItem.setExpirationDate(dto.getExpirationDate()); // Grava a validade no momento da criação
                    return newItem;
                });

        // Adiciona a quantidade (seja num lote existente com a mesma validade, ou num lote novo)
        inventory.setQuantity(inventory.getQuantity() + dto.getQuantity());
        inventoryItemRepository.save(inventory);

        // 3. Atualizar Preço de Venda (Se solicitado pelo dono)
        if (dto.getNewSalePrice() != null && dto.getNewSalePrice().compareTo(BigDecimal.ZERO) > 0) {
            product.setSalePrice(dto.getNewSalePrice());
            productRepository.save(product);
        }

        // 4. Cálculo do Custo Unitário (Preparação para a Tabela de Histórico Financeiro)
        BigDecimal unitCost = dto.getTotalCost()
                .divide(BigDecimal.valueOf(dto.getQuantity()), 2, RoundingMode.HALF_UP);

        if (financialService != null) {
            // Registra uma DESPESA no módulo financeiro
            financialService.registerTransaction(
                    "Compra de Estoque (NF: " + (dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : "S/N") + ") - " + product.getName(),
                    dto.getTotalCost(), // Pega o custo total preenchido no modal verde
                    TransactionType.EXPENSE, // Tipo Despesa (Saída)
                    "Compras de estoque", // A categoria exata que está na sua Dashboard!
                    null
            );
        }

        // 5. Gravar Histórico para Auditoria (Entrada)
        StockMovement movement = StockMovement.builder()
                .product(product)
                .sourceLocation(null) // Origem nula pois vem de fora (fornecedor)
                .destinationLocation(location)
                .quantity(dto.getQuantity())
                .movementDate(LocalDateTime.now())
                .type("ENTRY")
                .observation("Entrada via NF: " + (dto.getInvoiceNumber() != null ? dto.getInvoiceNumber() : "S/N"))
                .build();

        movementRepository.save(movement);
    }


    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<InventoryResponseDTO> searchStockForPos(Long locationId, String searchTerm) {
        return inventoryItemRepository.findAvailableStockForPos(locationId, searchTerm).stream()
                .map(item -> InventoryResponseDTO.builder()
                        .inventoryId(item.getId())
                        .productId(item.getProduct().getId())
                        .sku(item.getProduct().getSku())
                        .productName(item.getProduct().getName())
                        .brand(item.getProduct().getBrand())
                        .locationName(item.getLocation().getName())
                        .quantity(item.getQuantity())
                        .salePrice(item.getProduct().getSalePrice())
                        .expirationDate(item.getExpirationDate()) // <--- EXATAMENTE AQUI TAMBÉM!
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional
    public void executeDirectTransfer(StockTransferDTO dto) {
        // 1. Validação de Rota
        if (dto.getSourceLocationId().equals(dto.getDestinationLocationId())) {
            throw new RuntimeException("A origem e o destino não podem ser iguais.");
        }

        // 2. Buscar Lote na Origem
        InventoryItem sourceItem = inventoryItemRepository
                .findByProductIdAndLocationIdAndExpirationDate(dto.getProductId(), dto.getSourceLocationId(), dto.getExpirationDate())
                .orElseThrow(() -> new RuntimeException("Lote não encontrado na origem com esta validade."));

        // 3. Validar Quantidade
        if (sourceItem.getQuantity() < dto.getQuantity()) {
            throw new RuntimeException("Estoque insuficiente na origem. Disponível: " + sourceItem.getQuantity());
        }

        // 4. Buscar ou Criar Lote no Destino (Mantendo a mesma validade!)
        InventoryItem destinationItem = inventoryItemRepository
                .findByProductIdAndLocationIdAndExpirationDate(dto.getProductId(), dto.getDestinationLocationId(), dto.getExpirationDate())
                .orElseGet(() -> InventoryItem.builder()
                        .product(sourceItem.getProduct())
                        .location(locationRepository.findById(dto.getDestinationLocationId()).orElseThrow())
                        .quantity(0)
                        .inTransitQuantity(0)
                        .expirationDate(dto.getExpirationDate())
                        .build());

        // 5. Executar a Transferência Física
        sourceItem.setQuantity(sourceItem.getQuantity() - dto.getQuantity());
        destinationItem.setQuantity(destinationItem.getQuantity() + dto.getQuantity());

        inventoryItemRepository.save(sourceItem);
        inventoryItemRepository.save(destinationItem);

        // 6. Gravar Histórico para Auditoria
        StockMovement movement = StockMovement.builder()
                .product(sourceItem.getProduct())
                .sourceLocation(sourceItem.getLocation())
                .destinationLocation(destinationItem.getLocation())
                .quantity(dto.getQuantity())
                .movementDate(LocalDateTime.now())
                .type("TRANSFER")
                .observation("Transferência direta entre unidades")
                .build();

        movementRepository.save(movement);
    }

    public List<InventoryResponseDTO> findAllStock() {
        return inventoryItemRepository.findByQuantityGreaterThan(0).stream()
                .map(item -> InventoryResponseDTO.builder()
                        .inventoryId(item.getId())
                        .productId(item.getProduct().getId())
                        .sku(item.getProduct().getSku())
                        .productName(item.getProduct().getName())
                        .brand(item.getProduct().getBrand())
                        .locationName(item.getLocation().getName())
                        .quantity(item.getQuantity())
                        .salePrice(item.getProduct().getSalePrice())
                        .expirationDate(item.getExpirationDate()) // <--- EXATAMENTE AQUI!
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<StockMovementResponseDTO> findAllMovements() {
        return movementRepository.findAll().stream()
                .map(m -> {
                    StockMovementResponseDTO dto = new StockMovementResponseDTO();
                    dto.setId(m.getId());
                    dto.setProductName(m.getProduct().getName());
                    dto.setSourceLocationName(m.getSourceLocation() != null ? m.getSourceLocation().getName() : "ENTRADA EXTERNA");
                    dto.setDestinationLocationName(m.getDestinationLocation() != null ? m.getDestinationLocation().getName() : "SAÍDA (VENDA)");
                    dto.setQuantity(m.getQuantity());
                    dto.setMovementDate(m.getMovementDate());
                    dto.setType(m.getType());
                    dto.setObservation(m.getObservation());
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional
    public void registerSaleMovement(Product product, Location location, Integer quantity, String saleId) {
        StockMovement movement = StockMovement.builder()
                .product(product)
                .sourceLocation(location) // Sai da loja
                .destinationLocation(null) // Destino nulo = Saiu do sistema (Cliente levou)
                .quantity(quantity)
                .movementDate(LocalDateTime.now())
                .type("SALE")
                .observation("Venda realizada no PDV - Pedido #" + saleId)
                .build();

        movementRepository.save(movement);
    }
}
