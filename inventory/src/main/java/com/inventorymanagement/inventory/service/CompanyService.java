
package com.inventorymanagement.inventory.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.repository.CampanyRepository;

@Service
public class CompanyService {

    private final CampanyRepository companyRepository;

    public CompanyService(CampanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company insertCompany(Company company) {

        // Validate company name
        if (company.getName() == null ||
                company.getName().trim().isEmpty()) {

            throw new RuntimeException("Company name is required");
        }

        // Validate phone
        if (company.getPhone() == null ||
                company.getPhone().trim().isEmpty()) {

            throw new RuntimeException("Company phone is required");
        }

        // Validate address
        if (company.getAddress() == null ||
                company.getAddress().trim().isEmpty()) {

            throw new RuntimeException("Company address is required");
        }

        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company getCompanyById(Long id) {

        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));
    }

    public Company updateCompany(Long id, Company companyDetails) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        // Validate name
        if (companyDetails.getName() == null ||
                companyDetails.getName().trim().isEmpty()) {

            throw new RuntimeException("Company name is required");
        }

        // Validate phone
        if (companyDetails.getPhone() == null ||
                companyDetails.getPhone().trim().isEmpty()) {

            throw new RuntimeException("Company phone is required");
        }

        // Validate address
        if (companyDetails.getAddress() == null ||
                companyDetails.getAddress().trim().isEmpty()) {

            throw new RuntimeException("Company address is required");
        }

        company.setName(companyDetails.getName());
        company.setPhone(companyDetails.getPhone());
        company.setAddress(companyDetails.getAddress());

        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        companyRepository.delete(company);
    }
}

