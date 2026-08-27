package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.repository.ItemRepository;
import com.inventorymanagement.inventory.repository.StockBatchRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockBatchService {

    private final StockBatchRepository stockBatchRepository;
    private final ItemRepository itemRepository;

    public StockBatchService(
            StockBatchRepository stockBatchRepository,
            ItemRepository itemRepository) {

        this.stockBatchRepository = stockBatchRepository;
        this.itemRepository = itemRepository;
    }

    public StockBatch insertStockBatch(StockBatch stockBatch) {

        // Check item
        if (stockBatch.getItem() == null ||
                stockBatch.getItem().getId() == null) {

            throw new RuntimeException("Item is required");
        }

        Long itemId = stockBatch.getItem().getId();

        Items item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        stockBatch.setItem(item);

        // Check batch number
        if (stockBatch.getBatchNumber() == null ||
                stockBatch.getBatchNumber().trim().isEmpty()) {

            throw new RuntimeException("Batch number is required");
        }

        // Check quantity received
        if (stockBatch.getQuantityReceived() == null ||
                stockBatch.getQuantityReceived()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Quantity received must be greater than zero"
            );
        }

        // Set received date automatically
        if (stockBatch.getReceivedDate() == null) {
            stockBatch.setReceivedDate(LocalDate.now());
        }

        // Check expiry date
        if (stockBatch.getExpiryDate() == null) {
            throw new RuntimeException("Expiry date is required");
        }

        // Expiry date cannot be before received date
        if (stockBatch.getExpiryDate()
                .isBefore(stockBatch.getReceivedDate())) {

            throw new RuntimeException(
                    "Expiry date cannot be before received date"
            );
        }

        // New batch starts with full quantity
        stockBatch.setQuantityRemaining(
                stockBatch.getQuantityReceived()
        );

        return stockBatchRepository.save(stockBatch);
    }

    public List<StockBatch> getAllStockBatches() {
        return stockBatchRepository.findAll();
    }

    public StockBatch getStockBatchById(Long id) {
        return stockBatchRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Stock batch not found"));
    }
}