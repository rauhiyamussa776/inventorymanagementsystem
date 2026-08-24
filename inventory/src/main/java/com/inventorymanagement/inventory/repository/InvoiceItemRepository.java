package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
}