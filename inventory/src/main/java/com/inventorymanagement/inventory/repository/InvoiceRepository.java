package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}