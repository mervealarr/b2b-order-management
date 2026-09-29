package com.b2b.b2b_order_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.b2b.b2b_order_management.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    boolean existsByTaxNumber(String taxNumber);

    Optional<Company> findByTaxNumber(String taxNumber);

    boolean existsByEmail(String email);

}
