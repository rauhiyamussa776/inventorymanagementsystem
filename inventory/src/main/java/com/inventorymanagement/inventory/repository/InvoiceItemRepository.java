package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.Invoice;
import com.inventorymanagement.inventory.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoice(Invoice invoice);
}