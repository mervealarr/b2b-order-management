package com.b2b.b2b_order_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InventoryResponse {

    private Long id;
    private Long productId;
    private String productSku;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

}
