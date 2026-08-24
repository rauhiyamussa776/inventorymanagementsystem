package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.model.StockMovement;
import com.inventorymanagement.inventory.repository.ItemRepository;
import com.inventorymanagement.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ItemRepository itemRepository;

    public StockMovementService(
            StockMovementRepository stockMovementRepository,
            ItemRepository itemRepository) {

        this.stockMovementRepository = stockMovementRepository;
        this.itemRepository = itemRepository;
    }

    public StockMovement insertStockMovement(StockMovement stockMovement) {

        Long itemId = stockMovement.getItem().getId();

        Items item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        stockMovement.setItem(item);

        if (stockMovement.getMovementDate() == null) {
            stockMovement.setMovementDate(LocalDateTime.now());
        }

        return stockMovementRepository.save(stockMovement);
    }

    public List<StockMovement> getAllStockMovements() {
        return stockMovementRepository.findAll();
    }

    public StockMovement getStockMovementById(Long id) {
        return stockMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock movement not found"));
    }
}