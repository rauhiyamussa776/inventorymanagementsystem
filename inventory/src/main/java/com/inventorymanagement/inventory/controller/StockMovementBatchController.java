package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.StockMovementBatch;
import com.inventorymanagement.inventory.service.StockMovementBatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movement-batches")
public class StockMovementBatchController {

    private final StockMovementBatchService stockMovementBatchService;

    public StockMovementBatchController(
            StockMovementBatchService stockMovementBatchService) {

        this.stockMovementBatchService =
                stockMovementBatchService;
    }

    @GetMapping
    public List<StockMovementBatch> getAllStockMovementBatches() {

        return stockMovementBatchService
                .getAllStockMovementBatches();
    }

    @GetMapping("/{id}")
    public StockMovementBatch getStockMovementBatchById(
            @PathVariable Long id) {

        return stockMovementBatchService
                .getStockMovementBatchById(id);
    }
}