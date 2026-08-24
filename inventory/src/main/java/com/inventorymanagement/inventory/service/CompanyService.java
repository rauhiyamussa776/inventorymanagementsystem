package com.inventorymanagement.inventory.service;

import org.springframework.stereotype.Service;
import java.util.List;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.repository.CampanyRepository;

@Service
public class CompanyService {
    private final CampanyRepository companyRepository;

    public  CompanyService(CampanyRepository companyRepository){
        this.companyRepository = companyRepository;
    }
    public Company insertCompany(Company company){
        return companyRepository.save(company);
    }
    public List<Company>getAllCompanies(){
        return companyRepository.findAll();
    }
    public Company getCompanyById(Long id){
        return companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found"));
    }
    public Company updateCompany(Long id, Company companyDetails) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        company.setName(companyDetails.getName());
        company.setPhone(companyDetails.getPhone());
        company.setAddress(companyDetails.getAddress());

        return companyRepository.save(company);
    }
    public void deleteCompany(Long id){
        companyRepository.deleteById(id);
    }
}
