package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.StockMovementBatch;
import com.inventorymanagement.inventory.repository.StockMovementBatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMovementBatchService {

    private final StockMovementBatchRepository stockMovementBatchRepository;

    public StockMovementBatchService(
            StockMovementBatchRepository stockMovementBatchRepository) {

        this.stockMovementBatchRepository =
                stockMovementBatchRepository;
    }

    public List<StockMovementBatch> getAllStockMovementBatches() {

        return stockMovementBatchRepository.findAll();
    }

    public StockMovementBatch getStockMovementBatchById(Long id) {

        return stockMovementBatchRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Stock movement batch not found"
                        ));
    }
}