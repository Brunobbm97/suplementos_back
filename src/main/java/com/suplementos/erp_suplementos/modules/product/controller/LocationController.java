package com.suplementos.erp_suplementos.modules.product.controller;

import com.suplementos.erp_suplementos.modules.product.dto.LocationDTO;
import com.suplementos.erp_suplementos.modules.product.entity.Location;
import com.suplementos.erp_suplementos.modules.product.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @PostMapping
    public ResponseEntity<Location> create(@Valid @RequestBody LocationDTO dto) {
        Location saved = locationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> update(@PathVariable Long id, @Valid @RequestBody LocationDTO dto) {
        return ResponseEntity.ok(locationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}