package com.b2b.b2b_order_management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.b2b.b2b_order_management.dto.CompanyCreateRequest;
import com.b2b.b2b_order_management.dto.CompanyResponse;
import com.b2b.b2b_order_management.dto.CompanyUpdateRequest;
import com.b2b.b2b_order_management.entity.Company;
import com.b2b.b2b_order_management.repository.CompanyRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request) {
        if (companyRepository.existsByTaxNumber(request.getTaxNumber())) {
            throw new RuntimeException("Company with Tax Number " + request.getTaxNumber() + " already exists");
        }
        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Company with Email " + request.getEmail() + " already exists");
        }
        Company company = Company.builder()
                .name(request.getName())
                .taxNumber(request.getTaxNumber())
                .email(request.getEmail())
                .creditLimit(request.getCreditLimit())
                .active(true)
                .build();
        Company savedCompany = companyRepository.save(company);
        return mapToResponse(savedCompany);
    }

    private CompanyResponse mapToResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .taxNumber(company.getTaxNumber())
                .email(company.getEmail())
                .creditLimit(company.getCreditLimit())
                .active(company.getActive())
                .createdAt(company.getCreatedAt())
                .updatedAt(company.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found"));
        return mapToResponse(company);
    }

    @Transactional
    public List<CompanyResponse> getAllCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CompanyResponse updateCompany(Long id, CompanyUpdateRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company with ID " + id + " not found"));
        if (request.getName() != null) {
            company.setName(request.getName());
        }
        if (request.getEmail() != null) {
            company.setEmail(request.getEmail());
        }
        if (request.getCreditLimit() != null) {
            company.setCreditLimit(request.getCreditLimit());
        }
        if (request.getActive() != null) {
            company.setActive(request.getActive());
        }
        Company savedCompany = companyRepository.save(company);
        return mapToResponse(savedCompany);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company with ID " + id + " not found");
        }
        companyRepository.deleteById(id);
    }

}
