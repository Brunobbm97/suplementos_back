package com.suplementos.erp_suplementos.modules.sales.service;

import com.suplementos.erp_suplementos.modules.product.entity.InventoryItem;
import com.suplementos.erp_suplementos.modules.product.entity.Product;
import com.suplementos.erp_suplementos.modules.product.repository.InventoryItemRepository;
import com.suplementos.erp_suplementos.modules.product.repository.ProductRepository;
import com.suplementos.erp_suplementos.modules.sales.dto.request.SaleItemRequestDTO;
import com.suplementos.erp_suplementos.modules.sales.dto.request.SaleRequestDTO;
import com.suplementos.erp_suplementos.modules.sales.dto.response.SaleResponseDTO;
import com.suplementos.erp_suplementos.modules.sales.entity.Sale;
import com.suplementos.erp_suplementos.modules.sales.entity.SaleItem;
import com.suplementos.erp_suplementos.modules.sales.mapper.SaleMapper; // <-- NOVO
import com.suplementos.erp_suplementos.modules.sales.repository.SaleRepository;
import com.suplementos.erp_suplementos.modules.financial.service.FinancialService;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.product.service.InventoryService;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.product.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SaleRepository saleRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final ProductRepository productRepository;
    private final FinancialService financialService;
    private final SaleMapper saleMapper; // <-- NOVO
    private final InventoryService inventoryService; // 1. Injetar o serviço de inventário
    private final LocationRepository locationRepository; // Necessário para buscar a entidade Location

    @Transactional
    public void executeSale(SaleRequestDTO request) {

        // 1. O MapStruct cria a casca da Venda já com a Forma de Pagamento e o ID do Local
        Sale sale = saleMapper.toEntity(request);
        sale.setItems(new ArrayList<>());

        // Buscar a localização da venda para usar no histórico
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Localização não encontrada"));

        BigDecimal totalSale = BigDecimal.ZERO;

        // 2. A Regra de Negócio de Estoque (Intocável!)
        for (SaleItemRequestDTO itemReq : request.getItems()) {

            List<InventoryItem> batches = inventoryItemRepository
                    .findByProductIdAndLocationIdOrderByExpirationDateAsc(itemReq.getProductId(), request.getLocationId());

            int totalAvailable = batches.stream().mapToInt(InventoryItem::getQuantity).sum();
            if (totalAvailable < itemReq.getQuantity()) {
                throw new RuntimeException("Estoque insuficiente total para o produto: " + itemReq.getProductId());
            }

            int remainingToSell = itemReq.getQuantity();
            for (InventoryItem batch : batches) {
                if (remainingToSell <= 0) break;

                int quantityFromThisBatch = Math.min(batch.getQuantity(), remainingToSell);

                // Baixa no lote atual
                batch.setQuantity(batch.getQuantity() - quantityFromThisBatch);
                inventoryItemRepository.save(batch);

                remainingToSell -= quantityFromThisBatch;
            }
            // 4. Continua com a criação do item da venda (o preço vem do produto)
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow();
            BigDecimal unitPrice = product.getSalePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setProduct(product);
            saleItem.setQuantity(itemReq.getQuantity());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setSubtotal(subtotal);

            sale.getItems().add(saleItem);
            totalSale = totalSale.add(subtotal);

        }

        sale.setTotalAmount(totalSale);

        // 3. Salvar a Venda
        Sale savedSale = saleRepository.save(sale);

        // 2. REGISTRAR O MOVIMENTO DE ESTOQUE (O Rastro!)
        // Fazemos isso após salvar a venda para ter o ID do pedido no log
        for (SaleItem item : savedSale.getItems()) {
            inventoryService.registerSaleMovement(
                    item.getProduct(),
                    location,
                    item.getQuantity(),
                    String.valueOf(savedSale.getId())
            );
        }

        // 4. Registrar no Financeiro
        if (financialService != null) {
            financialService.registerTransaction(
                    "Venda PDV #" + savedSale.getId() + " - Unidade ID: " + request.getLocationId(),
                    totalSale,
                    TransactionType.REVENUE,
                    "Venda de Produtos",
                    savedSale.getId()
            );
        }
    }

    @Transactional(readOnly = true)
    public List<SaleResponseDTO> findAll() {
        return saleRepository.findAll().stream()
                .map(saleMapper::toResponseDTO) // A mágica do MapStruct acontece aqui
                .toList(); // Ou .collect(Collectors.toList()) se estiver no Java antigo
    }
}