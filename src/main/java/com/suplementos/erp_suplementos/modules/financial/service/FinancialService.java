package com.suplementos.erp_suplementos.modules.financial.service;

import com.suplementos.erp_suplementos.modules.financial.dto.FinancialSummaryDTO;
import com.suplementos.erp_suplementos.modules.financial.dto.FinancialTransactionResponseDTO;
import com.suplementos.erp_suplementos.modules.financial.entity.FinancialCategory;
import com.suplementos.erp_suplementos.modules.financial.entity.FinancialTransaction;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionStatus;
import com.suplementos.erp_suplementos.modules.financial.enums.TransactionType;
import com.suplementos.erp_suplementos.modules.financial.repository.FinancialCategoryRepository;
import com.suplementos.erp_suplementos.modules.financial.repository.FinancialTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final FinancialTransactionRepository transactionRepository;
    private final FinancialCategoryRepository categoryRepository;

    /**
     * MÉTODO DE BUSCA SEGURA: Se não existe, cria!
     * Isso impede que o sistema dê erro 500 em vendas e compras automáticas.
     */
    private FinancialCategory getOrCreateCategory(String categoryName, TransactionType type) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    FinancialCategory newCategory = new FinancialCategory();
                    newCategory.setName(categoryName);
                    newCategory.setType(type); // <--- O AJUSTE ESTÁ AQUI!
                    return categoryRepository.save(newCategory);
                });
    }

    /**
     * MÉTODO ORIGINAL (Compatibilidade com Vendas)
     * Não mudei a assinatura! Pode manter as chamadas de outros módulos aqui.
     */
    @Transactional
    public void registerTransaction(String description, BigDecimal amount, TransactionType type, String categoryName, Long referenceId) {
        this.registerAdvancedTransaction(
                description,
                amount,
                type,
                categoryName,
                referenceId,
                LocalDateTime.now(),
                TransactionStatus.PAGO,
                "Registro automático do sistema"
        );
    }

    /**
     * NOVO MÉTODO (Controle Manual / Agenda)
     * Use este método para a nova tela de finanças do dono.
     */
    @Transactional
    public void registerAdvancedTransaction(String description, BigDecimal amount, TransactionType type,
                                            String categoryName, Long referenceId, LocalDateTime dueDate,
                                            TransactionStatus status, String notes) {

        // Passamos o 'type' que veio do Front ou do módulo de Vendas
        FinancialCategory category = getOrCreateCategory(categoryName, type);

        FinancialTransaction transaction = new FinancialTransaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setReferenceId(referenceId);
        transaction.setNotes(notes);

        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setDueDate(dueDate != null ? dueDate : LocalDateTime.now());
        transaction.setStatus(status != null ? status : TransactionStatus.PAGO);

        if (transaction.getStatus() == TransactionStatus.PAGO) {
            transaction.setPaymentDate(LocalDateTime.now());
        }

        transactionRepository.save(transaction);
    }

    /**
     * MÉTODO DE BAIXA (A Liquidação)
     * Este é o método que o botão "Confirmar Pagamento" da sua lista vai chamar.
     */
    @Transactional
    public void confirmPayment(Long transactionId) {
        FinancialTransaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada ID: " + transactionId));

        if (transaction.getStatus() == TransactionStatus.PAGO) {
            throw new RuntimeException("Esta transação já foi paga anteriormente.");
        }

        transaction.setStatus(TransactionStatus.PAGO);
        transaction.setPaymentDate(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public FinancialSummaryDTO getBalanceSummary() {
        // DICA: No futuro, podemos filtrar aqui apenas transações com Status.PAGO
        // para o saldo não ficar "mentiroso" com previsões futuras.
        BigDecimal revenues = transactionRepository.sumPaidAmountByType(TransactionType.REVENUE);
        BigDecimal expenses = transactionRepository.sumPaidAmountByType(TransactionType.EXPENSE);

        revenues = (revenues != null) ? revenues : BigDecimal.ZERO;
        expenses = (expenses != null) ? expenses : BigDecimal.ZERO;

        BigDecimal balance = revenues.subtract(expenses);

        return new FinancialSummaryDTO(revenues, expenses, balance);
    }

    @Transactional(readOnly = true)
    public List<FinancialTransactionResponseDTO> findAll() {
        return transactionRepository.findAllByOrderByDueDateAsc().stream()
                .map(t -> FinancialTransactionResponseDTO.builder()
                        .id(t.getId())
                        .description(t.getDescription())
                        .amount(t.getAmount())
                        .type(t.getType())
                        .status(t.getStatus())
                        .categoryName(t.getCategory().getName()) // Pegamos apenas o nome
                        .dueDate(t.getDueDate())
                        .paymentDate(t.getPaymentDate())
                        .notes(t.getNotes())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTransaction(Long id, FinancialTransactionResponseDTO dto) {
        FinancialTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));

        // Atualizamos os campos básicos
        transaction.setDescription(dto.getDescription());
        transaction.setAmount(dto.getAmount());
        transaction.setDueDate(dto.getDueDate());
        transaction.setNotes(dto.getNotes());
        transaction.setType(dto.getType());

        // Lógica de Status: Se mudou para PAGO agora, setamos a data de pagamento
        if (transaction.getStatus() != TransactionStatus.PAGO && dto.getStatus() == TransactionStatus.PAGO) {
            transaction.setPaymentDate(LocalDateTime.now());
        }
        // Se mudou de PAGO para PENDENTE (correção de erro), limpamos a data de pagamento
        else if (dto.getStatus() == TransactionStatus.PENDENTE) {
            transaction.setPaymentDate(null);
        }

        transaction.setStatus(dto.getStatus());

        // Atualizamos a categoria se ela mudou
        if (!transaction.getCategory().getName().equals(dto.getCategoryName())) {
            FinancialCategory category = categoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
            transaction.setCategory(category);
        }

        transactionRepository.save(transaction);
    }
}