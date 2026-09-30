package com.b2b.b2b_order_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2b.b2b_order_management.dto.InventoryAddRequest;
import com.b2b.b2b_order_management.dto.InventoryResponse;
import com.b2b.b2b_order_management.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/add")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody InventoryAddRequest request) {
        return ResponseEntity.ok(inventoryService.addStock(request));
    }

    @PostMapping("/remove")
    public ResponseEntity<InventoryResponse> removeStock(@Valid @RequestBody InventoryAddRequest request) {
        return ResponseEntity.ok(inventoryService.removeStock(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventories() {
        return ResponseEntity.ok(inventoryService.getAllInventories());
    }
}
