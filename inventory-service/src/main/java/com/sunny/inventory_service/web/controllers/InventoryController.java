package com.sunny.inventory_service.web.controllers;

import com.sunny.inventory_service.entities.InventoryItem;
import com.sunny.inventory_service.repositories.InventoryItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/inventory")
@Slf4j
public class InventoryController {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @GetMapping("/{productCode}")
    public ResponseEntity<InventoryItem> findByProductCode(@PathVariable String productCode) {
        log.info("Finding inventory for product code: " + productCode);

        Optional<InventoryItem> inventoryItem = inventoryItemRepository.findByProductCode(productCode);

        if(inventoryItem.isPresent()) {
            return ResponseEntity.ok(inventoryItem.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public List<InventoryItem> getInventorys() {
        log.info("Finding inventory for all products ");
        return inventoryItemRepository.findAll();
    }
}
