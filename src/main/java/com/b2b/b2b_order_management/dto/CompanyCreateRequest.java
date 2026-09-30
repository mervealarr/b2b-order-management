package com.b2b.b2b_order_management.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyCreateRequest {

    @NotBlank(message = "Company name is required")
    private String name;

    @NotBlank(message = "Tax number is required")
    private String taxNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Credit limit is required")
    @DecimalMin(value = "0.0", message = "Credit limit cannot be negative")
    private BigDecimal creditLimit;
}
