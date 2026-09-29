package com.b2b.b2b_order_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class InventoryAddRequest {

    private Long productId;
    private Integer quantity;

}
