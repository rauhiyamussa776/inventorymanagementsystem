package com.inventorymanagement.inventory.service;

import com.inventorymanagement.inventory.model.Items;
import com.inventorymanagement.inventory.model.StockBatch;
import com.inventorymanagement.inventory.repository.ItemRepository;
import com.inventorymanagement.inventory.repository.StockBatchRepository;
import org.springframework.stereotype.Service;

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

    Long itemId = stockBatch.getItem().getId();

    Items item = itemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found"));

    stockBatch.setItem(item);

    if (stockBatch.getReceivedDate() == null) {
        stockBatch.setReceivedDate(LocalDate.now());
    }

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
                .orElseThrow(() -> new RuntimeException("Stock batch not found"));
    }
}