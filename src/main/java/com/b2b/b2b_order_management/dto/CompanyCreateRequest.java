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

public class CompanyCreateRequest {

    private String name;

    private String taxNumber;

    private String email;

    private BigDecimal creditLimit;

}
