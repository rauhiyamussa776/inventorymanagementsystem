package com.inventorymanagement.inventory.repository;

import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.model.Items;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface StockBatchRepository extends JpaRepository<StockBatch, Long> {

    List<StockBatch> findByItemAndQuantityRemainingGreaterThanOrderByReceivedDateAsc(
            Items item,
            BigDecimal quantityRemaining
    );
}