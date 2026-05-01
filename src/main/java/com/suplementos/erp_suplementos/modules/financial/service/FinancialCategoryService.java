package com.suplementos.erp_suplementos.modules.financial.service;

import com.suplementos.erp_suplementos.modules.financial.dto.FinancialCategoryDTO;
import com.suplementos.erp_suplementos.modules.financial.entity.FinancialCategory;
import com.suplementos.erp_suplementos.modules.financial.mapper.FinancialCategoryMapper;
import com.suplementos.erp_suplementos.modules.financial.repository.FinancialCategoryRepository;
// import com.suplementos.erp_suplementos.modules.financial.repository.FinancialTransactionRepository; // Descomente depois
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinancialCategoryService {

    private final FinancialCategoryRepository categoryRepository;
    private final FinancialCategoryMapper categoryMapper;

    // private final FinancialTransactionRepository transactionRepository; // Para a trava de exclusão

    @Transactional(readOnly = true)
    public List<FinancialCategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public FinancialCategoryDTO create(FinancialCategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Já existe uma categoria com o nome: " + dto.getName());
        }

        FinancialCategory category = categoryMapper.toEntity(dto);
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public FinancialCategoryDTO update(Long id, FinancialCategoryDTO dto) {
        FinancialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        // Só valida o nome duplicado se ele estiver tentando mudar para um nome que já existe em OUTRO ID
        if (!category.getName().equalsIgnoreCase(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Já existe outra categoria com o nome: " + dto.getName());
        }

        categoryMapper.updateEntityFromDTO(dto, category);
        category = categoryRepository.save(category);
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public void delete(Long id) {
        FinancialCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));

        // O LEÃO DE CHÁCARA: Trava de Segurança (Igual fizemos nos Locais)
        // boolean hasTransactions = transactionRepository.existsByCategoryId(id);
        // if (hasTransactions) {
        //     throw new RuntimeException("Não é possível excluir esta categoria pois já existem transações financeiras vinculadas a ela.");
        // }

        categoryRepository.delete(category);
    }
}