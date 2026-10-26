package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.service.StockBatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-batches")
public class StockBatchController {

    private final StockBatchService stockBatchService;

    public StockBatchController(StockBatchService stockBatchService) {
        this.stockBatchService = stockBatchService;
    }

    @PostMapping
    public StockBatch insertStockBatch(@RequestBody StockBatch stockBatch) {
        return stockBatchService.insertStockBatch(stockBatch);
    }

    @GetMapping
    public List<StockBatch> getAllStockBatches() {
        return stockBatchService.getAllStockBatches();
    }

    @GetMapping("/{id}")
    public StockBatch getStockBatchById(@PathVariable Long id) {
        return stockBatchService.getStockBatchById(id);
    }
}