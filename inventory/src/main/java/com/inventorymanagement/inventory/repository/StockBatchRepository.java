package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {
}