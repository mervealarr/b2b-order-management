package com.b2b.b2b_order_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.b2b.b2b_order_management.dto.InventoryAddRequest;
import com.b2b.b2b_order_management.dto.InventoryResponse;
import com.b2b.b2b_order_management.entity.Inventory;
import com.b2b.b2b_order_management.entity.Product;
import com.b2b.b2b_order_management.repository.InventoryRepository;
import com.b2b.b2b_order_management.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional
    public InventoryResponse addStock(InventoryAddRequest request) {
        if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid product ID or quantity");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + request.getProductId()));

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseGet(() -> Inventory.builder()
                        .product(product)
                        .quantity(0)
                        .reservedQuantity(0)
                        .build());

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    @Transactional
    public InventoryResponse removeStock(InventoryAddRequest request) {
        if (request.getProductId() == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Invalid product ID or quantity");
        }

        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + request.getProductId()));

        int reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
        int available = inventory.getQuantity() - reserved;

        if (available < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock to remove. Available: " + available + ", requested: " + request.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
        Inventory savedInventory = inventoryRepository.save(inventory);

        return mapToResponse(savedInventory);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product ID: " + productId));
        return mapToResponse(inventory);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        int reserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
        int available = inventory.getQuantity() - reserved;

        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productSku(inventory.getProduct().getSku())
                .quantity(inventory.getQuantity())
                .reservedQuantity(reserved)
                .availableQuantity(available)
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
