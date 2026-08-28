
package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Company;
import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceStatus;
import com.inventorymanagement.inventory.repository.CampanyRepository;
import com.inventorymanagement.inventory.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // Check company
        if (invoice.getCompany() == null ||
                invoice.getCompany().getId() == null) {

            throw new RuntimeException("Company is required");
        }

        // Find company
        Long companyId = invoice.getCompany().getId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new RuntimeException("Company not found"));

        invoice.setCompany(company);

        // Check invoice number
        if (invoice.getInvoiceNumber() == null ||
                invoice.getInvoiceNumber().trim().isEmpty()) {

            throw new RuntimeException(
                    "Invoice number is required"
            );
        }

        // Check invoice date
        if (invoice.getInvoiceDate() == null) {

            invoice.setInvoiceDate(LocalDate.now());
        }

        // Check due date
        if (invoice.getDueDate() == null) {

            throw new RuntimeException(
                    "Due date is required"
            );
        }

        // Due date cannot be before invoice date
        if (invoice.getDueDate()
                .isBefore(invoice.getInvoiceDate())) {

            throw new RuntimeException(
                    "Due date cannot be before invoice date"
            );
        }

        // Set default values
        if (invoice.getTotalAmount() == null) {

            invoice.setTotalAmount(0.0);
        }

        if (invoice.getPaidAmount() == null) {

            invoice.setPaidAmount(0.0);
        }

        // New invoice starts as UNPAID
        invoice.setStatus(InvoiceStatus.UNPAID);

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {

        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {

        return invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));
    }
}
