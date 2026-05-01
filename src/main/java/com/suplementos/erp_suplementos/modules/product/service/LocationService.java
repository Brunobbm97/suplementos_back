package com.suplementos.erp_suplementos.modules.product.service;

import com.suplementos.erp_suplementos.modules.product.dto.LocationDTO;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.product.mapper.LocationMapper;
import com.suplementos.erp_suplementos.modules.product.repository.InventoryItemRepository;
import com.suplementos.erp_suplementos.modules.product.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final LocationMapper locationMapper; // <-- Injetando o Mapper

    @Transactional(readOnly = true)
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    @Transactional
    public Location create(LocationDTO dto) {
        Location location = locationMapper.toEntity(dto); // 1 linha mágica
        return locationRepository.save(location);
    }

    @Transactional
    public Location update(Long id, LocationDTO dto) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado."));

        locationMapper.updateEntityFromDTO(dto, location); // 1 linha mágica
        return locationRepository.save(location);
    }

    @Transactional
    public void delete(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Local não encontrado."));

        boolean hasStock = inventoryItemRepository.findByQuantityGreaterThan(0).stream()
                .anyMatch(item -> item.getLocation().getId().equals(id));

        if (hasStock) {
            throw new RuntimeException("Não é possível excluir este local pois ainda existem produtos em estoque nele. Transfira-os primeiro.");
        }

        locationRepository.delete(location);
    }
}