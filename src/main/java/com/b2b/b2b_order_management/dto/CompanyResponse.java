package com.b2b.b2b_order_management.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class CompanyResponse {

    private Long id;
    private String name;
    private String taxNumber;
    private String email;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private Boolean active;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;
}
