package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.StockMovementBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementBatchRepository
        extends JpaRepository<StockMovementBatch, Long> {
}