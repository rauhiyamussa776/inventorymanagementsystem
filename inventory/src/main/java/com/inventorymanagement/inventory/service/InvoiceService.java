package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.repository.CampanyRepository;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CampanyRepository companyRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            CampanyRepository companyRepository) {

        this.invoiceRepository = invoiceRepository;
        this.companyRepository = companyRepository;
    }

    public Invoice insertInvoice(Invoice invoice) {

        Long companyId = invoice.getCompany().getId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        invoice.setCompany(company);

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }
}