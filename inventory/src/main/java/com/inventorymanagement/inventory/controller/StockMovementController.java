package com.inventorymanagement.inventory.controller;

import com.inventorymanagement.inventory.model.StockMovement;
import com.inventorymanagement.inventory.service.StockMovementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    public StockMovement insertStockMovement(
            @RequestBody StockMovement stockMovement) {

        return stockMovementService.insertStockMovement(stockMovement);
    }

    @GetMapping
    public List<StockMovement> getAllStockMovements() {
        return stockMovementService.getAllStockMovements();
    }

    @GetMapping("/{id}")
    public StockMovement getStockMovementById(@PathVariable Long id) {
        return stockMovementService.getStockMovementById(id);
    }
}